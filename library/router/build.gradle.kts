import configures.libraryConfigure
import build.BuildModules

libraryConfigure(projectConfigure = {
    dependencies {
        commonProcessors()
        implementation(project(BuildModules.Library.base))
    }
})


