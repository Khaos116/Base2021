package com.cc.base2021.dialog

import android.view.Gravity
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.StringUtils
import com.cc.base.ui.BaseDialogFragment
import com.cc.base2021.R
import kotlinx.android.synthetic.main.dialog_action.dialogActionHint

/**
 * Author:case
 * Date:2020/8/11
 * Time:19:39
 */
class ActionDialog : BaseDialogFragment() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //默认提示语
  var hintText: String = StringUtils.getString(R.string.action_loading)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override val contentXmlId = R.layout.dialog_action
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="懒加载">
  override fun lazyInit() {
    dialogActionHint?.text = hintText
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部调用弹窗">
  //开始弹窗
  fun show(fragmentManager: FragmentManager) {
    dialogActionHint?.text = hintText
    show(fragmentManager, "ActionDialog")
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部调用实例">
  companion object {
    fun newInstance(
      touchCancel: Boolean = true
    ): ActionDialog {
      val dialog = ActionDialog()
      dialog.mGravity = Gravity.CENTER
      dialog.touchOutside = touchCancel
      dialog.mWidth = ScreenUtils.getScreenWidth() / 3
      return dialog
    }
  }
  //</editor-fold>
}

//<editor-fold defaultstate="collapsed" desc="DSL调用">
inline fun actionDialog(fragmentManager: FragmentManager, dsl: ActionDialog.() -> Unit) {
  val dialog = ActionDialog.newInstance().apply(dsl)
  dialog.show(fragmentManager)
}
//</editor-fold>