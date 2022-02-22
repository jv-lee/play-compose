import configures.libraryConfigure
import build.BuildModules

libraryConfigure(projectConfigure = {
    dependencies {
        commonProcessors()
        api(project(BuildModules.Library.base))
        api(project(BuildModules.Library.common))
        api(project(BuildModules.Library.router))
    }
})


