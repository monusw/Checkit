package xin.monus.checkit.util

import android.content.Context
import xin.monus.checkit.data.source.local.ActionLocalDataSource
import xin.monus.checkit.data.source.local.DailyLocalDataSource
import xin.monus.checkit.data.source.local.InboxItemLocalDataSource
import xin.monus.checkit.data.source.local.ProjectsLocalDataSource
import xin.monus.checkit.data.source.repository.ActionRepository
import xin.monus.checkit.data.source.repository.DailyRepository
import xin.monus.checkit.data.source.repository.InboxItemRepository
import xin.monus.checkit.data.source.repository.ProjectsRepository


object Injection {

    @JvmStatic fun getInboxItemRepository(context: Context) : InboxItemRepository =
            InboxItemRepository.getInstance(InboxItemLocalDataSource.getInstance(context))

    @JvmStatic fun getProjectsRepository(context: Context) : ProjectsRepository =
            ProjectsRepository.getInstance(ProjectsLocalDataSource.getInstance(context))

    @JvmStatic fun getActionRepository(context: Context) : ActionRepository =
            ActionRepository.getInstance(ActionLocalDataSource.getInstance(context))

    @JvmStatic fun getDailyRepository(context: Context) : DailyRepository =
            DailyRepository.getInstance(DailyLocalDataSource.getInstance(context))
}