package org.fossasia.susi.ai.data.model

import java.util.ArrayList

data class TableItem(
    var columns: ArrayList<String>? = null,
    var tableData: ArrayList<String>? = null
)
