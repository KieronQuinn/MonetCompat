package com.kieronquinn.monetcompat.extensions.views

import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.*
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.core.MonetMaterialException
import kotlin.reflect.KClass

/**
 *  Select view types to theme or use [MonetAutoThemeableViews.ALL] to select all of them,
 *  or [MonetAutoThemeableViews.ALL_NO_MATERIAL] for all that don't require the Material library
 *  @param viewType One or more View(s) to handle
 */
sealed class MonetAutoThemeableViews(vararg val viewType: KClass<out View>) {

    companion object {
        val ALL: Array<MonetAutoThemeableViews>
            get() {
                try {
                    CircularProgressIndicator::class.java
                }catch (e: NoClassDefFoundError){
                    throw MonetMaterialException()
                }
                return arrayOf(
                    BOTTOM_NAVIGATION,
                    BUTTON,
                    BUTTON_MATERIAL,
                    CHECKBOX,
                    CHECKBOX_MATERIAL,
                    CIRCULAR_PROGRESS_INDICATOR,
                    EDIT_TEXT,
                    FLOATING_ACTION_BUTTON,
                    LINEAR_PROGRESS_INDICATOR,
                    PROGRESS_BAR,
                    RADIO_BUTTON,
                    RADIO_BUTTON_MATERIAL,
                    RECYCLER_VIEW,
                    SCROLL_VIEW,
                    SEEKBAR,
                    SLIDER,
                    SWITCH,
                    TEXT_INPUT
                )
        }

        val ALL_NO_MATERIAL get() = arrayOf(
            BUTTON,
            CHECKBOX,
            EDIT_TEXT,
            PROGRESS_BAR,
            RADIO_BUTTON,
            RECYCLER_VIEW,
            SCROLL_VIEW,
            SEEKBAR,
            SWITCH
        )
    }

    /**
     *  Note: [BOTTOM_NAVIGATION] does not change the background color by default, you must do
     *  that manually or call [BottomNavigationView.applyMonet] yourself, passing
     *  setBackgroundColor = true
     */
    object BOTTOM_NAVIGATION : MonetAutoThemeableViews(BottomNavigationView::class)
    object BUTTON : MonetAutoThemeableViews(Button::class, AppCompatButton::class)
    object BUTTON_MATERIAL : MonetAutoThemeableViews(MaterialButton::class)
    object CHECKBOX : MonetAutoThemeableViews(CheckBox::class, AppCompatCheckBox::class)
    object CHECKBOX_MATERIAL : MonetAutoThemeableViews(MaterialCheckBox::class)
    object CIRCULAR_PROGRESS_INDICATOR : MonetAutoThemeableViews(CircularProgressIndicator::class)
    object EDIT_TEXT : MonetAutoThemeableViews(EditText::class, AppCompatEditText::class)
    object FLOATING_ACTION_BUTTON : MonetAutoThemeableViews(FloatingActionButton::class, ExtendedFloatingActionButton::class)
    object LINEAR_PROGRESS_INDICATOR : MonetAutoThemeableViews(LinearProgressIndicator::class)
    object PROGRESS_BAR : MonetAutoThemeableViews(ProgressBar::class)
    object RADIO_BUTTON : MonetAutoThemeableViews(RadioButton::class, AppCompatRadioButton::class)
    object RADIO_BUTTON_MATERIAL : MonetAutoThemeableViews(MaterialRadioButton::class)
    object RECYCLER_VIEW : MonetAutoThemeableViews(RecyclerView::class)
    object SCROLL_VIEW : MonetAutoThemeableViews(NestedScrollView::class, ScrollView::class, HorizontalScrollView::class)
    object SEEKBAR : MonetAutoThemeableViews(SeekBar::class, AppCompatSeekBar::class)
    object SLIDER : MonetAutoThemeableViews(Slider::class)
    object SWITCH : MonetAutoThemeableViews(SwitchCompat::class)
    object TEXT_INPUT : MonetAutoThemeableViews(TextInputEditText::class, TextInputLayout::class)
}

/**
 *  Apply Monet to all child views to this View recursively, so long as it can be cast to [ViewGroup].
 *  @param themeableViews You can optionally pass a list of [MonetAutoThemeableViews] to
 *  apply Monet to if you don't want to theme them all (which is the default)
 *  @param customThemeables An optional list of handlers for specified view types.
 *  Use [customThemeableView] to construct handlers, passing one or view types that can be cast to
 *  a single View
 */
fun View.applyMonetRecursively(
    vararg themeableViews: MonetAutoThemeableViews = MonetAutoThemeableViews.ALL,
    customThemeables: List<Pair<Array<out KClass<out View>>, (View, MonetCompat) -> Unit>>? = null
) {
    if(this !is ViewGroup) return
    val viewTypesToTheme = themeableViews.flatMap { it.viewType.map { clazz -> clazz } }
    applyMonetToViewGroup(viewTypesToTheme, customThemeables)
}

/**
 *  Create a handler for a custom view. Pass this into [applyMonetRecursively] to apply your block
 *  to Views that are instances of the classes listed in [viewTypes]
 *  @param T The base View type all the [viewTypes] extend from (or the same View if there is only
 *  one [viewTypes])
 *  @param viewTypes One or more View classes to apply the block to. Be aware that both AppCompat
 *  and MaterialComponents will automatically change some Views to their own variants, so you may
 *  need to include some extra views, such as [MaterialTextView] and/or [AppCompatTextView] for
 *  [TextView]
 *  @param themeable What to do with the View to theme it. You will be passed the View (cast to [T])
 *  and the [MonetCompat] instance to make calls on.
 */
@Suppress("UNCHECKED_CAST")
fun <T> customThemeableView(
    vararg viewTypes: KClass<out View>,
    themeable: (T, MonetCompat) -> Unit
) = Pair(viewTypes, themeable as (View, MonetCompat) -> Unit)

private fun ViewGroup.applyMonetToViewGroup(
    viewTypesToTheme: List<KClass<out View>>,
    customThemeables: List<Pair<Array<out KClass<out View>>, (View, MonetCompat) -> Unit>>?
) {
    checkAndApplyMonetToView(viewTypesToTheme, customThemeables)
    children.forEach {
        if (it is ViewGroup) {
            it.applyMonetToViewGroup(viewTypesToTheme, customThemeables)
        } else {
            it.checkAndApplyMonetToView(viewTypesToTheme, customThemeables)
        }
    }
}

private fun View.checkAndApplyMonetToView(
    viewTypesToTheme: List<KClass<out View>>,
    customThemeables: List<Pair<Array<out KClass<out View>>, (View, MonetCompat) -> Unit>>?
) {
    if (viewTypesToTheme.contains(this::class)) {
        this.applyMonetToGenericView()
    }
    customThemeables?.forEach { customThemeable ->
        if (customThemeable.first.contains(this::class)) {
            customThemeable.second.invoke(this, MonetCompat.getInstance())
        }
    }
}

private fun View.applyMonetToGenericView() {
    when (this) {
        is RadioButton -> applyMonet()
        is CheckBox -> applyMonet()
        is FloatingActionButton -> applyMonet()
        is ExtendedFloatingActionButton -> applyMonet()
        is Slider -> applyMonet()
        is SwitchCompat -> applyMonet()
        is TextInputLayout -> applyMonet()
        is TextInputEditText -> applyMonet()
        is MaterialButton -> applyMonet()
        is BottomNavigationView -> applyMonet()
        is BaseProgressIndicator<*> -> applyMonet()
        is RecyclerView -> applyMonet()
        is NestedScrollView -> applyMonet()
        is ScrollView -> applyMonet()
        is HorizontalScrollView -> applyMonet()
        // == GENERICS ==
        is EditText -> applyMonet()
        is Button -> applyMonet()
        is SeekBar -> applyMonet()
        is ProgressBar -> applyMonet()
    }
}