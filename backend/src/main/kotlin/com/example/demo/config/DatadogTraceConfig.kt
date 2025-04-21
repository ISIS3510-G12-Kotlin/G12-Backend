// File: com/example/demo/config/DatadogTraceConfig.kt
package com.example.demo.config

import com.timgroup.statsd.NonBlockingStatsDClient
import com.timgroup.statsd.NonBlockingStatsDClientBuilder
import com.timgroup.statsd.StatsDClient
import datadog.trace.api.DDTags
import datadog.trace.api.Trace
import io.opentracing.Tracer
import io.opentracing.util.GlobalTracer
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import jakarta.annotation.PostConstruct
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.FilterConfig
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Configuration
@EnableAspectJAutoProxy
class DatadogTraceConfig : WebMvcConfigurer {

    private val logger = LoggerFactory.getLogger(DatadogTraceConfig::class.java)

    @Value("\${dd.service.name:explorandes-backend}")
    private lateinit var serviceName: String

    @Value("\${dd.agent.host:localhost}")
    private lateinit var agentHost: String

    @Value("\${dd.agent.port:8126}")
    private var agentPort: Int = 8126

    @Value("\${dd.trace.enabled:true}")
    private var traceEnabled: Boolean = true

    @Value("\${dd.logs.injection:true}")
    private var logsInjectionEnabled: Boolean = true

    @Value("\${dd.profiling.enabled:false}")
    private var profilingEnabled: Boolean = false

    @Value("\${dd.env:development}")
    private lateinit var environment: String

    @PostConstruct
    fun init() {
        logger.info("Initializing Datadog configuration with service name: {}", serviceName)
        
        // Set system properties for Datadog agent
        System.setProperty("dd.agent.host", agentHost)
        System.setProperty("dd.agent.port", agentPort.toString())
        System.setProperty("dd.service.name", serviceName)
        System.setProperty("dd.logs.injection", logsInjectionEnabled.toString())
        System.setProperty("dd.trace.enabled", traceEnabled.toString())
        System.setProperty("dd.profiling.enabled", profilingEnabled.toString())
        System.setProperty("dd.env", environment)
    }

    @Bean
    fun tracer(): Tracer {
        // Since the Datadog Java agent is loaded via javaagent, 
        // we can get the OpenTracing compatible tracer directly
        val tracer = if (GlobalTracer.isRegistered()) {
            GlobalTracer.get()
        } else {
            val noopTracer = io.opentracing.noop.NoopTracerFactory.create()
            if (traceEnabled) {
                GlobalTracer.registerIfAbsent(noopTracer)
            }
            noopTracer
        }
        
        logger.info("Tracer initialized: {}", tracer.javaClass.name)
        return tracer
    }

    @Bean
    fun statsDClient(): StatsDClient {
        val builder = NonBlockingStatsDClientBuilder()
            .prefix(serviceName)
            .hostname(agentHost)
            .port(8125)
            .constantTags("env:$environment")
        
        return builder.build()
    }

    @Bean
    fun datadogTracingFilter(): Filter {
        return DatadogTracingFilter(tracer())
    }

    @Bean
    fun restTemplate(): RestTemplate {
        val restTemplate = RestTemplate()
        restTemplate.interceptors.add(DatadogRestTemplateInterceptor(tracer()))
        return restTemplate
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(DatadogWebRequestInterceptor(tracer(), statsDClient()))
    }
}

// Web request interceptor to trace incoming HTTP requests
class DatadogWebRequestInterceptor(
    private val tracer: Tracer,
    private val statsDClient: StatsDClient
) : org.springframework.web.servlet.HandlerInterceptor {

    private val logger = LoggerFactory.getLogger(DatadogWebRequestInterceptor::class.java)
    
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val uri = request.requestURI
        val method = request.method
        
        // Create metric for API request count
        statsDClient.incrementCounter("api.request.count", "endpoint:$uri", "method:$method")
        
        // Start timer for API response time
        request.setAttribute("requestStartTime", System.currentTimeMillis())
        
        return true
    }
    
    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        val uri = request.requestURI
        val method = request.method
        val status = response.status
        
        // Calculate request duration
        val startTime = request.getAttribute("requestStartTime") as Long?
        startTime?.let {
            val duration = System.currentTimeMillis() - it
            
            // Record response time metric
            statsDClient.recordExecutionTime(
                "api.response.time",
                duration,
                "endpoint:$uri",
                "method:$method",
                "status:$status"
            )
            
            // Log request details
            logger.info("API Request: $method $uri, Status: $status, Duration: ${duration}ms")
        }
        
        // Track API errors
        if (status >= 400) {
            statsDClient.incrementCounter(
                "api.errors",
                "endpoint:$uri",
                "method:$method",
                "status:$status"
            )
        }
    }
}

// A filter to trace incoming HTTP requests
class DatadogTracingFilter(private val tracer: Tracer) : Filter {
    
    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain
    ) {
        if (request !is HttpServletRequest || response !is HttpServletResponse) {
            chain.doFilter(request, response)
            return
        }
        
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse
        
        val spanBuilder = tracer.buildSpan("http.request")
            .withTag("http.url", httpRequest.requestURL.toString())
            .withTag("http.method", httpRequest.method)
            
        tracer.activeSpan()?.let { spanBuilder.asChildOf(it) }
        
        val span = spanBuilder.start()
        
        try {
            chain.doFilter(request, response)
            
            span.setTag("http.status_code", httpResponse.status)
            
            if (httpResponse.status >= 400) {
                span.setTag("error", true)
                span.setTag("error.type", "http")
                span.setTag("error.msg", "HTTP ${httpResponse.status}")
            }
            
        } catch (e: Exception) {
            span.setTag("error", true)
            span.setTag("error.type", e.javaClass.name)
            span.setTag("error.msg", e.message ?: "Unknown error")
            span.setTag("error.stack", e.stackTraceToString())
            throw e
        } finally {
            span.finish()
        }
    }
    
    override fun init(filterConfig: FilterConfig) {}
    
    override fun destroy() {}
}

// REST template interceptor for tracing outbound HTTP requests
class DatadogRestTemplateInterceptor(private val tracer: Tracer) : org.springframework.http.client.ClientHttpRequestInterceptor {
    
    override fun intercept(
        request: org.springframework.http.HttpRequest,
        body: ByteArray,
        execution: org.springframework.http.client.ClientHttpRequestExecution
    ): org.springframework.http.client.ClientHttpResponse {
        
        val span = tracer.buildSpan("http.client.request")
            .withTag("http.url", request.uri.toString())
            .withTag("http.method", request.method.toString())
            .start()
            
        try {
            val response = execution.execute(request, body)
            
            span.setTag("http.status_code", response.statusCode.value())
            
            if (response.statusCode.value() >= 400) {
                span.setTag("error", true)
                span.setTag("error.type", "http")
                span.setTag("error.msg", "HTTP ${response.statusCode.value()}")
            }
            
            return response
        } catch (e: Exception) {
            span.setTag("error", true)
            span.setTag("error.type", e.javaClass.name)
            span.setTag("error.msg", e.message ?: "Unknown error")
            span.setTag("error.stack", e.stackTraceToString())
            throw e
        } finally {
            span.finish()
        }
    }
}

// Aspect for tracking method execution time and adding traces
@Aspect
@Component
class DatadogTraceAspect(
    private val statsDClient: StatsDClient,
    private val tracer: Tracer
) {
    private val logger = LoggerFactory.getLogger(DatadogTraceAspect::class.java)
    
    @Around("execution(* com.example.demo.service..*.*(..))")
    fun traceServiceMethods(joinPoint: ProceedingJoinPoint): Any? {
        val start = System.currentTimeMillis()
        val className = joinPoint.signature.declaringTypeName
        val methodName = joinPoint.signature.name
        val metricName = "service.execution.time"
        val tags = arrayOf("class:$className", "method:$methodName")
        
        val spanBuilder = tracer.buildSpan("$className.$methodName")
        val span = spanBuilder.start()
        
        try {
            return joinPoint.proceed()
        } catch (e: Exception) {
            span.setTag("error", true)
            span.setTag("error.type", e.javaClass.name)
            span.setTag("error.msg", e.message ?: "Unknown error")
            
            // Track exception in StatsD as well
            statsDClient.incrementCounter("service.errors", *tags, "error:${e.javaClass.simpleName}")
            
            throw e
        } finally {
            span.finish()
            
            val elapsedTime = System.currentTimeMillis() - start
            statsDClient.recordExecutionTime(metricName, elapsedTime, *tags)
            
            if (logger.isDebugEnabled) {
                logger.debug("Method $className.$methodName executed in ${elapsedTime}ms")
            }
        }
    }
    
    // Special aspect specifically for database operations
    @Around("execution(* com.example.demo.repository..*.*(..))")
    fun traceDatabaseMethods(joinPoint: ProceedingJoinPoint): Any? {
        val start = System.currentTimeMillis()
        val className = joinPoint.signature.declaringTypeName
        val methodName = joinPoint.signature.name
        val metricName = "db.query.time"
        val tags = arrayOf("repository:$className", "method:$methodName")
        
        val spanBuilder = tracer.buildSpan("db.$className.$methodName")
            .withTag("db.type", "postgresql")
        val span = spanBuilder.start()
        
        try {
            return joinPoint.proceed()
        } catch (e: Exception) {
            span.setTag("error", true)
            span.setTag("error.type", e.javaClass.name)
            span.setTag("error.msg", e.message ?: "Unknown error")
            
            // Track DB exception in StatsD
            statsDClient.incrementCounter("db.errors", *tags, "error:${e.javaClass.simpleName}")
            
            throw e
        } finally {
            span.finish()
            
            val elapsedTime = System.currentTimeMillis() - start
            statsDClient.recordExecutionTime(metricName, elapsedTime, *tags)
            
            if (logger.isDebugEnabled) {
                logger.debug("Database operation $className.$methodName executed in ${elapsedTime}ms")
            }
        }
    }
}