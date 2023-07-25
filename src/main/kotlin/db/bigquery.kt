package db

import com.google.cloud.bigquery.*

    fun getOrCreateDataset(datasetName: String, location: String): Dataset {
        val bigquery: BigQuery = BigQueryOptions.getDefaultInstance()
            .service
        val datasetInfo = DatasetInfo.newBuilder(datasetName)
            .setLocation(location)
            .build()
        return bigquery.create(datasetInfo) ?: bigquery.getDataset(datasetName)
    }

fun getOrCreateTable(datasetName: String, tableName: String, schema: Schema): Table {
    val bigquery = BigQueryOptions.getDefaultInstance()
        .service
    val tableId = TableId.of(datasetName, tableName)
    val tableDefinition = StandardTableDefinition.of(schema)
    val tableInfo = TableInfo.newBuilder(tableId, tableDefinition).build()
    return bigquery.create(tableInfo) ?: bigquery.getTable(tableId)
}
