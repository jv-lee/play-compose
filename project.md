# PlayCompose技术文档

# 问题 - TODO
1. TextFiled存在内存泄露，在输入内容后退出该页面就会出现内存泄露，原因是内部焦点无法释放导致。 需要在退出页面时调用
   LocalFocusManager.clearFocus()清除焦点即可解决， 如果使用LocalSoftwareKeyboardController.hide()依然会有该问题。
