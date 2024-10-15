import build.BuildModules
import configures.libraryConfigure
import configures.plugins.paramsConfigure


libraryConfigure("common", projectConfigure = {
    paramsConfigure()

    dependencies {
        commonProcessors()
        implementation(project(BuildModules.Library.base))
    }
})


