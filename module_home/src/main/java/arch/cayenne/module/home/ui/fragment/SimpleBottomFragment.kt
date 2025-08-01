package arch.cayenne.module.home.ui.fragment


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.module.home.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * @date: 2025/8/1 17:37
 * @description:
 */
class SimpleBottomFragment : BottomSheetDialogFragment() {
    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        "aaaa----onCreate".logd(TAG)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        "aaaa----onCreateDialog".logd(TAG)
        val dialog = object : BottomSheetDialog(requireContext(), theme) {
            override fun onBackPressed() {
                dismiss()
                super.onBackPressed()
            }
        }

        dialog.setOnShowListener {
            "aaaa----onShowListener".logd(TAG)

        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_test_empty,container,false)
        return root
    }
}