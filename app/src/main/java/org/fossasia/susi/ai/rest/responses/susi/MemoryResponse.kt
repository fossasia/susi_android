package org.fossasia.susi.ai.rest.responses.susi

/**
 * <h1>Kotlin Data class to parse retrofit response from memory endpoint susi client.</h1>

 * Created by chiragw15 on 25/5/17.
 */

class MemoryResponse (

    var cognitions: List<SusiResponse> = ArrayList(),
    var session: Session? = null

)
