package org.secfirst.umbrella.whitelabel.feature.account.view

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.account_password_alert.view.*
import kotlinx.android.synthetic.main.account_skip_alert.view.*
import kotlinx.android.synthetic.main.account_view.*
import kotlinx.android.synthetic.main.account_view.view.*
import kotlinx.android.synthetic.main.mask_app_info_view.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.feature.account.DaggerAccountComponent
import org.secfirst.umbrella.whitelabel.feature.account.interactor.AccountBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.account.presenter.AccountBasePresenter
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.maskapp.view.CalculatorController
import org.secfirst.umbrella.whitelabel.misc.checkPasswordStrength
import setMaskMode
import javax.inject.Inject

class AccountController : BaseController(), AccountView {

    @Inject
    internal lateinit var presenter: AccountBasePresenter<AccountView, AccountBaseInteractor>
    private lateinit var passwordAlertDialog: AlertDialog
    private lateinit var maskAppAlertDialog: AlertDialog
    private lateinit var skipPasswordDialog: AlertDialog
    private lateinit var passwordView: View
    private lateinit var maskAppView: View
    private lateinit var skipPasswordView: View

    override fun onInject() {
        DaggerAccountComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        setUpToolbar()
        enableNavigation()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

        val accountView = inflater.inflate(R.layout.account_view, container, false)
        passwordView = inflater.inflate(R.layout.account_password_alert, container, false)
        skipPasswordView = inflater.inflate(R.layout.account_skip_alert, container, false)
        maskAppView = inflater.inflate(R.layout.mask_app_info_view, container, false)

        maskAppAlertDialog = AlertDialog
                .Builder(activity)
                .setView(maskAppView)
                .create()

        skipPasswordDialog = AlertDialog
                .Builder(activity)
                .setView(skipPasswordView)
                .create()
        passwordAlertDialog = AlertDialog
                .Builder(activity)
                .setView(passwordView)
                .create()
        presenter.onAttach(this)

        accountView.accountSettings.setOnClickListener { clickOnSettings() }
        accountView.accountPassword.setOnClickListener { clickOnPasswordAlert() }
        accountView.accountMask.setOnClickListener { clickOnMaskApp() }

        passwordView.passwordSkip.setOnClickListener { clickOnSkipAlert() }
        passwordView.passwordOk.setOnClickListener { passwordAlertOk() }
        passwordView.passwordCancel.setOnClickListener { passwordAlertCancel() }

        skipPasswordView.cancel.setOnClickListener { skipAlertCancel() }
        skipPasswordView.ok.setOnClickListener { skipAlertOk() }

        maskAppView.handsShakeCancel.setOnClickListener { maskAppCancel() }
        maskAppView.handsShakeOk.setOnClickListener { maskAppOk() }
        return accountView
    }

    private fun passwordAlertCancel() = passwordAlertDialog.dismiss()

    private fun skipAlertCancel() = skipPasswordDialog.dismiss()

    private fun maskAppOk() {
        presenter.setMaskApp(true)
        //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        setMaskMode(activity!!, true)
        maskAppAlertDialog.dismiss()
        presenter.setMaskApp(true)
        router.pushController(RouterTransaction.with(CalculatorController()))
    }

    private fun maskAppCancel() = maskAppAlertDialog.dismiss()

    private fun clickOnMaskApp() = maskAppAlertDialog.show()

    private fun clickOnPasswordAlert() = passwordAlertDialog.show()

    private fun skipAlertOk() {
        presenter.setSkipPassword(true)
        skipPasswordDialog.dismiss()
    }

    private fun passwordAlertOk() {
        val token = passwordView.pwText.text.toString()
        if (token.checkPasswordStrength(context))
            presenter.submitChangeDatabaseAccess(token)
    }

    private fun clickOnSkipAlert() {
        skipPasswordDialog.show()
        passwordAlertDialog.dismiss()
    }

    private fun setUpToolbar() {
        accountToolbar?.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.title = context.getString(R.string.settings_title)
        }
    }

    private fun clickOnSettings() {
        router.pushController(RouterTransaction.with(SettingsController()))
    }

    override fun isLogged(res: Boolean) {
        if (res) {
            passwordAlertDialog.dismiss()
            presenter.setUserLogIn()
            Toast.makeText(context, "Password created with success.", Toast.LENGTH_SHORT).show()
        }
    }
}