package com.walisport.app.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.ui.viewmodel.BaseActivityViewModel
import arch.cayenne.lib.skin.LanguageManager
import arch.cayenne.lib.skin.SkinnableManager
import com.walisport.app.data.repo.SplashRepository
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import java.util.Locale

class SplashViewModel : BaseActivityViewModel() {

    private val repository: SplashRepository by inject { parametersOf(viewModelScope) }
    private val skinManager: SkinnableManager by inject { parametersOf(viewModelScope) }
    private val languageManager:LanguageManager by inject { parametersOf(viewModelScope) }

    override val shouldBeAutoLogin: Boolean = false

    //加载皮肤和语言方案
    fun loadMyAppSkin() {
        viewModelScope.launch {
            val skinType = repository.getSkinType()
            skinManager.loadSkin(skinType)
            val langType = repository.getLanguageType()
            languageManager.changeLanguage(Locale(langType))
        }
    }
}