package com.cc.base.ext

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * Author:case
 * Date:2020/8/11
 * Time:17:47
 */
fun EditText.addTextWatcher(after: (s: Editable?) -> Unit) {
  this.addTextChangedListener(object : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
      after.invoke(s)
    }

    override fun beforeTextChanged(
      s: CharSequence?,
      start: Int,
      count: Int,
      after: Int
    ) {
    }

    override fun onTextChanged(
      s: CharSequence?,
      start: Int,
      before: Int,
      count: Int
    ) {
    }

  })
}