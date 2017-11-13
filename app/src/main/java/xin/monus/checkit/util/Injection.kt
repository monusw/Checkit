package xin.monus.checkit.util

import android.content.Context
import xin.monus.checkit.data.source.local.InboxItemLocalDataSource
import xin.monus.checkit.data.source.repository.InboxItemRepository


object Injection {

    @JvmStatic fun getInboxItemRepository(context: Context) : InboxItemRepository {
        return InboxItemRepository.getInstance(InboxItemLocalDataSource.getInstance(context))
    }
}