package org.fossasia.susi.ai.profile

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_profile.user_email
import kotlinx.android.synthetic.main.fragment_profile.user_name
import kotlinx.android.synthetic.main.fragment_profile.profile_image
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.data.UtilModel
import org.fossasia.susi.ai.helper.Constant
import org.fossasia.susi.ai.helper.PrefManager

class ProfileFragment : Fragment() {
    private lateinit var utilModel: UtilModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        utilModel = UtilModel(requireContext())
        getDatas()
        if (!utilModel.isLoggedIn()) {
            user_email.setOnClickListener {
                //To be implemented after issue #2048 is merged
            }
        } else {
            user_email.isClickable = false
        }
        super.onViewCreated(view, savedInstanceState)
    }

    fun getDatas() {
        //Dealing with user name
        val user_name_data = PrefManager.getString(Constant.USER_NAME, Constant.SUSI)
        if (user_name_data == Constant.SUSI) {
            user_name.text = getString(R.string.no_name_added)
        } else {
            user_name.setText(user_name_data)
        }

        //Dealing with user email
        if (!utilModel.isLoggedIn()) {
            user_email.text = getString(R.string.not_logged_in)
        } else {
            user_email.text = PrefManager.getStringSet(Constant.SAVED_EMAIL)?.iterator()?.next()
        }

        val profile_image_path = PrefManager.getString(Constant.PROFILE_IMAGE_PATH, Constant.SUSI)
        if (profile_image_path != Constant.SUSI) {
            profile_image.setImageBitmap(BitmapFactory.decodeFile(profile_image_path))
        }
    }
}
