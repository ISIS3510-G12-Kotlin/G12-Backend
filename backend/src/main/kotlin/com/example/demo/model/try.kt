
val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .setRequiresBatteryNotLow(true)
    .build()

val workRequest = OneTimeWorkRequestBuilder<ArtworkUpdateWorker>()
    .setConstraints(constraints)
    .build()

WorkManager.getInstance(context).enqueue(workRequest)

