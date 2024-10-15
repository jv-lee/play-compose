import configures.libraryConfigure
import build.BuildModules

libraryConfigure("router", projectConfigure = {
    dependencies {
        commonProcessors()
        implementation(project(BuildModules.Library.base))
    }
})


