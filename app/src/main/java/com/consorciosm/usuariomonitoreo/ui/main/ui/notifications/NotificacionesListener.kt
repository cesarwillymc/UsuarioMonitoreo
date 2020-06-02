package com.consorciosm.usuariomonitoreo.ui.main.ui.notifications

import com.consorciosm.usuariomonitoreo.data.model.NotificacionesList

interface NotificacionesListener {
    fun listener(notificacionesList: NotificacionesList,position:Int)
}