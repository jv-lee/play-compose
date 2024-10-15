import configures.libraryConfigure
import build.BuildModules

libraryConfigure("service", projectConfigure = {
    dependencies {
        commonProcessors()
        api(project(BuildModules.Library.base))
        api(project(BuildModules.Library.common))
        api(project(BuildModules.Library.router))
    }
})


