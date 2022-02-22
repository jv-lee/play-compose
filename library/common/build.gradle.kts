import build.BuildModules
import configures.libraryConfigure
import configures.plugins.paramsConfigure


libraryConfigure(projectConfigure = {
    paramsConfigure()

    dependencies {
        commonProcessors()
        implementation(project(BuildModules.Library.base))
    }
})


