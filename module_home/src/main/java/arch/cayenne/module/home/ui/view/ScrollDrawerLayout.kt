package arch.cayenne.module.home.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout

//reference-https://github.com/Mindinventory/minavdrawer
class ScrollDrawerLayout : DrawerLayout {
    private var drawerListener: DrawerListener? = null

    constructor(context: Context) : this(context, null)
    var mScrollDrawerEvents: ScrollDrawerEvents? = null

    @SuppressLint("CustomViewStyleable")
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        setDrawerView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private fun setDrawerView() {
        if (drawerListener != null) {
            // Remove callback after adding new listener with new type.
            removeDrawerListener(drawerListener!!)
        }
        drawerListener = object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                // Getting content view.
                val contentView = getChildAt(0)

                // Getting navigation view.
                val navigationView = getChildAt(1)

                // Check app configuration for RTL or LTR
                val config = context.resources.configuration

                 if (navigationView != null) {
                        if (config.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                            contentView!!.translationX = -(navigationView.width * slideOffset)
                        } else {
                            contentView!!.translationX = navigationView.width * slideOffset
                        }
                    }
                }


            override fun onDrawerOpened(drawerView: View) {
                if (mScrollDrawerEvents != null) {
                    mScrollDrawerEvents?.onDrawerOpened(drawerView)
                }
            }

            override fun onDrawerClosed(drawerView: View) {
                if (mScrollDrawerEvents != null) {
                    mScrollDrawerEvents?.onDrawerClosed(drawerView)
                }
            }

            override fun onDrawerStateChanged(newState: Int) {

            }
        }
        addDrawerListener(drawerListener!!)
    }

    /**
     * Set the listener for the drawer open and close events.
     */
    fun setScrollDrawerListener(mDrawerEvents: ScrollDrawerEvents) {
        this.mScrollDrawerEvents = mDrawerEvents
    }

    /**
     * Interface for the drawer events.
     */
    interface ScrollDrawerEvents {
        fun onDrawerOpened(drawerView: View) {
        }
        fun onDrawerClosed(drawerView: View) {
        }
    }
}