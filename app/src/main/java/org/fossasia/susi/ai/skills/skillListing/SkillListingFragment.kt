package org.fossasia.susi.ai.skills.skillListing

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_skill_listing.*
import org.fossasia.susi.ai.R
import org.fossasia.susi.ai.skills.SkillsActivity
import org.fossasia.susi.ai.skills.skillListing.contract.ISkillListingPresenter
import org.fossasia.susi.ai.skills.skillListing.contract.ISkillListingView

/**
 *
 * Created by chiragw15 on 15/8/17.
 */
class SkillListingFragment: Fragment(), ISkillListingView {

    lateinit var skillListingPresenter: ISkillListingPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_skill_listing, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        skillListingPresenter = SkillListingPresenter(activity as SkillsActivity)
        skillListingPresenter.onAttach(this)
        skillListingPresenter.getGroups()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun visibilityProgressBar(boolean: Boolean) {
        if(boolean) skillWait.visibility = View.VISIBLE else View.GONE
    }


}