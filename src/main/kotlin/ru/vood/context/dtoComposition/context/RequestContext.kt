package ru.vood.context.dtoComposition.context

import ru.vood.context.dtoComposition.AbstractBaseContext
import ru.vood.context.dtoComposition.ClientDetails
import ru.vood.context.dtoComposition.RequestInfo

data class RequestContext(val requestInfo: RequestInfo) : AbstractBaseContext<RequestContext>() {

    fun withClient(clientId: String, clientDetails: ClientDetails): ClientContext {
        require(clientId == clientDetails.id) { "Client ID mismatch" }
        return ClientContext(this, clientId, clientDetails)
    }
}
