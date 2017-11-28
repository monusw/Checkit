package xin.monus.checkit.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.Action
import xin.monus.checkit.data.entity.InboxItem
import xin.monus.checkit.data.entity.Project
import xin.monus.checkit.data.entity.ProjectType
import xin.monus.checkit.data.source.ProjectsDataSource
import xin.monus.checkit.db.LocalDbHelper
import xin.monus.checkit.util.Injection


class TestActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        if (LocalDbHelper.deleteDatabase(this)) {
            println("delete database success")
        } else {
            println("no database to be deleted")
        }

//        val dbHelper = LocalDbHelper(this)
//        with(dbHelper.writableDatabase) {
//            execSQL(LocalDbHelper.OPEN_FOREIGN_KEYS)
//            execSQL("INSERT INTO USER(username, password, nickname)" +
//                    "    VALUES ('test', 'hello', 'myth');")
//            execSQL("INSERT INTO USER(username, password, nickname)" +
//                    "    VALUES ('test1', 'hello', 'myth');")
//            execSQL("INSERT INTO INBOX_ITEM (username, content, deadline, complete)" +
//                    "  VALUES ('test1', 'complete homework', DATETIME('2017-12-08 17:00'), 1 );")
//            execSQL("INSERT INTO INBOX_ITEM (username, content, deadline, complete)" +
//                    "  VALUES ('test1', 'homework', DATETIME('2017-12-08 17:00'), 1 );")
//            execSQL("INSERT INTO INBOX_ITEM (username, content, deadline, complete)" +
//                    "  VALUES ('test1', 'review', DATETIME('2017-12-08 17:00'), 1 );")
////            execSQL("DELETE FROM USER WHERE username = 'test1';")
//        }
        FakeData.generateData(this)

        val item = InboxItem(username = "test",
                content = "smart ass",
                deadline = "2017-12-23 18:00",
                complete = false,
                flag = true
                )

        val action = Action(
                projectId = 1,
                content = "smart ass",
                deadline = "2017-12-23 18:00",
                complete = false,
                flag = true,
                subActionList = ArrayList(0)
        )

        val project = Project(
                username = "test",
                content = "ass",
                type = ProjectType.PARALLEL,
                deadline = "2017-12-23 18:00",
                complete = false,
                flag = true,
                actionList = ArrayList(0)
        )

//        val ldb = InboxItemLocalDataSource.getInstance(this)
        val ldb = Injection.getProjectsRepository(this)

        ldb.addProject(project, object : ProjectsDataSource.OperationCallback {
            override fun success() {
                println("add success")
            }

            override fun fail() {
                println("add failed")
            }

        })

        ldb.deleteProject(1, object : ProjectsDataSource.OperationCallback {
            override fun success() {
                println("delete success")
            }

            override fun fail() {
                println("delete failed")
            }

        })

        ldb.getProjectById(2, object : ProjectsDataSource.GetProjectCallback {
            override fun onProjectLoaded(project: Project) {
                project.content = "faQ"
                ldb.updateProject(project, object : ProjectsDataSource.OperationCallback {
                    override fun success() {
                        println("update success")
                    }

                    override fun fail() {
                        println("update fail")
                    }
                })
            }

            override fun onDataNotAvailable() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

        ldb.getProjects(object : ProjectsDataSource.LoadProjectsCallback {
            override fun onProjectsLoaded(projects: List<Project>) {
                for (i in projects) {
                    println(i.content)
                }
            }

            override fun onDataNotAvailable() {
                println("no actions")
            }

        })






//        val user = UserProfile.getUser(this)
    }


}