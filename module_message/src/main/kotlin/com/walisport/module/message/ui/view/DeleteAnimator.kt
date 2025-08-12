package com.walisport.module.message.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlin.math.max

class DeleteAnimator : SimpleItemAnimator() {

    private var sDefaultInterpolator: TimeInterpolator? = null
    private val mPendingRemovals = ArrayList<ViewHolder>()
    private val mPendingAdditions = ArrayList<ViewHolder>()
    private val mPendingMoves: ArrayList<MoveInfo> = ArrayList()
    private val mPendingChanges: ArrayList<ChangeInfo> = ArrayList()

    private val mAdditionsList = ArrayList<ArrayList<ViewHolder>>()
    private val mMovesList: ArrayList<ArrayList<MoveInfo>> = ArrayList()
    private val mChangesList: ArrayList<ArrayList<ChangeInfo>> = ArrayList()

    private val mAddAnimations = ArrayList<ViewHolder>()
    private val mMoveAnimations = ArrayList<ViewHolder>()
    private val mRemoveAnimations = ArrayList<ViewHolder>()
    private val mChangeAnimations = ArrayList<ViewHolder>()

    private val animDuration = 150L

    class MoveInfo(
        var holder: ViewHolder,
        var fromX: Int,
        var fromY: Int,
        var toX: Int,
        var toY: Int
    )

    class ChangeInfo private constructor(
        var oldHolder: ViewHolder,
        var newHolder: ViewHolder
    ) {
        var fromX: Int = 0
        var fromY: Int = 0
        var toX: Int = 0
        var toY: Int = 0

        constructor(
            oldHolder: ViewHolder, newHolder: ViewHolder,
            fromX: Int, fromY: Int, toX: Int, toY: Int
        ) : this(oldHolder, newHolder) {
            this.fromX = fromX
            this.fromY = fromY
            this.toX = toX
            this.toY = toY
        }

        override fun toString(): String {
            return ("ChangeInfo{"
                    + "oldHolder=" + oldHolder
                    + ", newHolder=" + newHolder
                    + ", fromX=" + fromX
                    + ", fromY=" + fromY
                    + ", toX=" + toX
                    + ", toY=" + toY
                    + '}')
        }
    }

    override fun runPendingAnimations() {
        val removalsPending = mPendingRemovals.isNotEmpty()
        val movesPending = mPendingMoves.isNotEmpty()
        val changesPending = mPendingChanges.isNotEmpty()
        val additionsPending = mPendingAdditions.isNotEmpty()
        if (!removalsPending && !movesPending && !additionsPending && !changesPending) {
            return
        }
        for (holder: ViewHolder? in mPendingRemovals) {
            animateRemoveImpl(holder!!)
        }
        mPendingRemovals.clear()
        if (movesPending) {
            val moves: ArrayList<MoveInfo> = ArrayList()
            moves.addAll(mPendingMoves)
            mMovesList.add(moves)
            mPendingMoves.clear()
            val mover = Runnable {
                for (moveInfo: MoveInfo in moves) {
                    animateMoveImpl(
                        moveInfo.holder, moveInfo.fromX, moveInfo.fromY,
                        moveInfo.toX, moveInfo.toY
                    )
                }
                moves.clear()
                mMovesList.remove(moves)
            }
            if (removalsPending) {
                val view: View = moves[0].holder.itemView
                ViewCompat.postOnAnimationDelayed(view, mover, animDuration)
            } else {
                mover.run()
            }
        }
        if (changesPending) {
            val changes: ArrayList<ChangeInfo> = ArrayList()
            changes.addAll(mPendingChanges)
            mChangesList.add(changes)
            mPendingChanges.clear()
            val changer = Runnable {
                for (change: ChangeInfo? in changes) {
                    if (change != null) {
                        animateChangeImpl(change)
                    }
                }
                changes.clear()
                mChangesList.remove(changes)
            }
            if (removalsPending) {
                val holder: ViewHolder = changes[0].oldHolder
                ViewCompat.postOnAnimationDelayed(holder.itemView, changer, animDuration)
            } else {
                changer.run()
            }
        }
        if (additionsPending) {
            val additions = ArrayList<ViewHolder>()
            additions.addAll(mPendingAdditions)
            mAdditionsList.add(additions)
            mPendingAdditions.clear()
            val adder = Runnable {
                for (holder: ViewHolder? in additions) {
                    if (holder != null) {
                        animateAddImpl(holder)
                    }
                }
                additions.clear()
                mAdditionsList.remove(additions)
            }
            if (removalsPending || movesPending || changesPending) {
                val removeDuration: Long = if (removalsPending) animDuration else 0
                val moveDuration: Long = if (movesPending) animDuration else 0
                val changeDuration: Long = if (changesPending) animDuration else 0
                val totalDelay =
                    (removeDuration + max(
                        moveDuration.toDouble(),
                        changeDuration.toDouble()
                    )).toLong()
                val view = additions[0].itemView
                ViewCompat.postOnAnimationDelayed(view, adder, totalDelay)
            } else {
                adder.run()
            }
        }
    }

    override fun animateRemove(holder: ViewHolder): Boolean {
        resetAnimation(holder)
        mPendingRemovals.add(holder)
        return true
    }

    private fun animateRemoveImpl(holder: ViewHolder) {
        val view = holder.itemView
        val animation = view.animate()
        mRemoveAnimations.add(holder)
        animation.setDuration(animDuration).alpha(0f).setListener(
            object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animator: Animator) {
                    dispatchRemoveStarting(holder)
                }

                override fun onAnimationEnd(animator: Animator) {
                    animation.setListener(null)
                    view.alpha = 1f
                    dispatchRemoveFinished(holder)
                    mRemoveAnimations.remove(holder)
                    dispatchFinishedWhenDone()
                }
            }).start()
    }

    override fun animateAdd(holder: ViewHolder): Boolean {
        resetAnimation(holder)
        holder.itemView.alpha = 0f
        mPendingAdditions.add(holder)
        return true
    }

    private fun animateAddImpl(holder: ViewHolder) {
        val view = holder.itemView
        val animation = view.animate()
        mAddAnimations.add(holder)
        animation.alpha(1f).setDuration(animDuration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animator: Animator) {
                    dispatchAddStarting(holder)
                }

                override fun onAnimationCancel(animator: Animator) {
                    view.alpha = 1f
                }

                override fun onAnimationEnd(animator: Animator) {
                    animation.setListener(null)
                    dispatchAddFinished(holder)
                    mAddAnimations.remove(holder)
                    dispatchFinishedWhenDone()
                }
            }).start()
    }

    override fun animateMove(
        holder: ViewHolder, fromX: Int, fromY: Int,
        toX: Int, toY: Int
    ): Boolean {
        var fromXPos = fromX
        var fromYPos = fromY
        val view = holder.itemView
        fromXPos += holder.itemView.translationX.toInt()
        fromYPos += holder.itemView.translationY.toInt()
        resetAnimation(holder)
        val deltaX = toX - fromXPos
        val deltaY = toY - fromYPos
        if (deltaX == 0 && deltaY == 0) {
            dispatchMoveFinished(holder)
            return false
        }
        if (deltaX != 0) {
            view.translationX = -deltaX.toFloat()
        }
        if (deltaY != 0) {
            view.translationY = -deltaY.toFloat()
        }
        mPendingMoves.add(MoveInfo(holder, fromX, fromY, toX, toY))
        return true
    }

    private fun animateMoveImpl(holder: ViewHolder, fromX: Int, fromY: Int, toX: Int, toY: Int) {
        val view = holder.itemView
        val deltaX = toX - fromX
        val deltaY = toY - fromY
        if (deltaX != 0) {
            view.animate().translationX(0f)
        }
        if (deltaY != 0) {
            view.animate().translationY(0f)
        }
        val animation = view.animate()
        mMoveAnimations.add(holder)
        animation.setDuration(150L).setListener(object : AnimatorListenerAdapter() {

            override fun onAnimationStart(animator: Animator) {
                dispatchMoveStarting(holder)
            }

            override fun onAnimationCancel(animator: Animator) {
                if (deltaX != 0) {
                    view.translationX = 0f
                }
                if (deltaY != 0) {
                    view.translationY = 0f
                }
            }

            override fun onAnimationEnd(animator: Animator) {
                animation.setListener(null)
                dispatchMoveFinished(holder)
                mMoveAnimations.remove(holder)
                dispatchFinishedWhenDone()
            }
        }).start()
    }

    override fun animateChange(
        oldHolder: ViewHolder, newHolder: ViewHolder?,
        fromX: Int, fromY: Int, toX: Int, toY: Int
    ): Boolean {
        if (oldHolder === newHolder) {
            return animateMove(oldHolder, fromX, fromY, toX, toY)
        }
        val prevTranslationX = oldHolder.itemView.translationX
        val prevTranslationY = oldHolder.itemView.translationY
        val prevAlpha = oldHolder.itemView.alpha
        resetAnimation(oldHolder)
        val deltaX = (toX - fromX - prevTranslationX).toInt()
        val deltaY = (toY - fromY - prevTranslationY).toInt()
        oldHolder.itemView.translationX = prevTranslationX
        oldHolder.itemView.translationY = prevTranslationY
        oldHolder.itemView.alpha = prevAlpha
        if (newHolder != null) {
            resetAnimation(newHolder)
            newHolder.itemView.translationX = -deltaX.toFloat()
            newHolder.itemView.translationY = -deltaY.toFloat()
            newHolder.itemView.alpha = 0f
        }
        mPendingChanges.add(ChangeInfo(oldHolder, newHolder!!, fromX, fromY, toX, toY))
        return true
    }

    private fun animateChangeImpl(changeInfo: ChangeInfo) {
        val holder: ViewHolder = changeInfo.oldHolder
        val view = if (holder == null) null else holder.itemView
        val newHolder: ViewHolder = changeInfo.newHolder
        val newView = if (newHolder != null) newHolder.itemView else null
        if (view != null) {
            val oldViewAnim = view.animate().setDuration(animDuration)
            mChangeAnimations.add(changeInfo.oldHolder)
            oldViewAnim.translationX((changeInfo.toX - changeInfo.fromX).toFloat())
            oldViewAnim.translationY((changeInfo.toY - changeInfo.fromY).toFloat())
            oldViewAnim.alpha(0f).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animator: Animator) {
                    dispatchChangeStarting(changeInfo.oldHolder, true)
                }

                override fun onAnimationEnd(animator: Animator) {
                    oldViewAnim.setListener(null)
                    view.alpha = 1f
                    view.translationX = 0f
                    view.translationY = 0f
                    dispatchChangeFinished(changeInfo.oldHolder, true)
                    mChangeAnimations.remove(changeInfo.oldHolder)
                    dispatchFinishedWhenDone()
                }
            }).start()
        }
        if (newView != null) {
            val newViewAnimation = newView.animate()
            mChangeAnimations.add(changeInfo.newHolder)
            newViewAnimation.translationX(0f).translationY(0f).setDuration(animDuration)
                .alpha(1f).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animator: Animator) {
                        dispatchChangeStarting(changeInfo.newHolder, false)
                    }

                    override fun onAnimationEnd(animator: Animator) {
                        newViewAnimation.setListener(null)
                        newView.alpha = 1f
                        newView.translationX = 0f
                        newView.translationY = 0f
                        dispatchChangeFinished(changeInfo.newHolder, false)
                        mChangeAnimations.remove(changeInfo.newHolder)
                        dispatchFinishedWhenDone()
                    }
                }).start()
        }
    }

    private fun endChangeAnimation(infoList: MutableList<ChangeInfo>, item: ViewHolder) {
        for (i in infoList.indices.reversed()) {
            val changeInfo: ChangeInfo = infoList[i]
            if (endChangeAnimationIfNecessary(changeInfo, item)) {
                if (changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                    infoList.remove(changeInfo)
                }
            }
        }
    }

    private fun endChangeAnimationIfNecessary(changeInfo: ChangeInfo) {
        if (changeInfo.oldHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.oldHolder)
        }
        if (changeInfo.newHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.newHolder)
        }
    }

    private fun endChangeAnimationIfNecessary(changeInfo: ChangeInfo, item: ViewHolder): Boolean {
        var oldItem = false
        if (changeInfo.newHolder === item) {

        } else if (changeInfo.oldHolder === item) {
            oldItem = true
        } else {
            return false
        }
        item.itemView.alpha = 1f
        item.itemView.translationX = 0f
        item.itemView.translationY = 0f
        dispatchChangeFinished(item, oldItem)
        return true
    }

    override fun endAnimation(item: ViewHolder) {
        val view = item.itemView
        view.animate().cancel()
        for (i in mPendingMoves.indices.reversed()) {
            val moveInfo: MoveInfo = mPendingMoves[i]
            if (moveInfo.holder === item) {
                view.translationY = 0f
                view.translationX = 0f
                dispatchMoveFinished(item)
                mPendingMoves.removeAt(i)
            }
        }
        endChangeAnimation(mPendingChanges, item)
        if (mPendingRemovals.remove(item)) {
            view.alpha = 1f
            dispatchRemoveFinished(item)
        }
        if (mPendingAdditions.remove(item)) {
            view.alpha = 1f
            dispatchAddFinished(item)
        }

        for (i in mChangesList.indices.reversed()) {
            val changes: ArrayList<ChangeInfo> = mChangesList[i]
            endChangeAnimation(changes, item)
            if (changes.isEmpty()) {
                mChangesList.removeAt(i)
            }
        }
        for (i in mMovesList.indices.reversed()) {
            val moves: ArrayList<MoveInfo> = mMovesList[i]
            for (j in moves.indices.reversed()) {
                val moveInfo: MoveInfo = moves[j]
                if (moveInfo.holder === item) {
                    view.translationY = 0f
                    view.translationX = 0f
                    dispatchMoveFinished(item)
                    moves.removeAt(j)
                    if (moves.isEmpty()) {
                        mMovesList.removeAt(i)
                    }
                    break
                }
            }
        }
        for (i in mAdditionsList.indices.reversed()) {
            val additions = mAdditionsList[i]
            if (additions.remove(item)) {
                view.alpha = 1f
                dispatchAddFinished(item)
                if (additions.isEmpty()) {
                    mAdditionsList.removeAt(i)
                }
            }
        }

        // animations should be ended by the cancel above.
        if (mRemoveAnimations.remove(item)) {
            throw IllegalStateException(
                "after animation is cancelled, item should not be in "
                        + "mRemoveAnimations list"
            )
        }

        if (mAddAnimations.remove(item)) {
            throw IllegalStateException(
                ("after animation is cancelled, item should not be in "
                        + "mAddAnimations list")
            )
        }

        if (mChangeAnimations.remove(item)) {
            throw IllegalStateException(
                ("after animation is cancelled, item should not be in "
                        + "mChangeAnimations list")
            )
        }

        if (mMoveAnimations.remove(item)) {
            throw IllegalStateException(
                ("after animation is cancelled, item should not be in "
                        + "mMoveAnimations list")
            )
        }
        dispatchFinishedWhenDone()
    }

    private fun resetAnimation(holder: ViewHolder) {
        if (sDefaultInterpolator == null) {
            sDefaultInterpolator = ValueAnimator().interpolator
        }
        holder.itemView.animate().setInterpolator(sDefaultInterpolator)
        endAnimation(holder)
    }

    override fun isRunning(): Boolean {
        return (((mPendingAdditions.isNotEmpty()
                || mPendingChanges.isNotEmpty()
                || mPendingMoves.isNotEmpty()
                || mPendingRemovals.isNotEmpty()
                || mMoveAnimations.isNotEmpty()
                || mRemoveAnimations.isNotEmpty()
                || mAddAnimations.isNotEmpty()
                || mChangeAnimations.isNotEmpty()
                || mMovesList.isNotEmpty()
                || mAdditionsList.isNotEmpty()
                || mChangesList.isNotEmpty())))
    }

    fun dispatchFinishedWhenDone() {
        if (!isRunning()) {
            dispatchAnimationsFinished()
        }
    }

    override fun endAnimations() {
        var count = mPendingMoves.size
        for (i in count - 1 downTo 0) {
            val item: MoveInfo = mPendingMoves[i]
            val view: View = item.holder.itemView
            view.translationY = 0f
            view.translationX = 0f
            dispatchMoveFinished(item.holder)
            mPendingMoves.removeAt(i)
        }
        count = mPendingRemovals.size
        for (i in count - 1 downTo 0) {
            val item = mPendingRemovals[i]
            dispatchRemoveFinished(item)
            mPendingRemovals.removeAt(i)
        }
        count = mPendingAdditions.size
        for (i in count - 1 downTo 0) {
            val item = mPendingAdditions[i]
            item.itemView.alpha = 1f
            dispatchAddFinished(item)
            mPendingAdditions.removeAt(i)
        }
        count = mPendingChanges.size
        for (i in count - 1 downTo 0) {
            endChangeAnimationIfNecessary(mPendingChanges[i])
        }
        mPendingChanges.clear()
        if (!isRunning()) {
            return
        }

        var listCount = mMovesList.size
        for (i in listCount - 1 downTo 0) {
            val moves: ArrayList<MoveInfo> = mMovesList[i]
            count = moves.size
            for (j in count - 1 downTo 0) {
                val moveInfo: MoveInfo = moves[j]
                val item: ViewHolder = moveInfo.holder
                val view = item.itemView
                view.translationY = 0f
                view.translationX = 0f
                dispatchMoveFinished(moveInfo.holder)
                moves.removeAt(j)
                if (moves.isEmpty()) {
                    mMovesList.remove(moves)
                }
            }
        }
        listCount = mAdditionsList.size
        for (i in listCount - 1 downTo 0) {
            val additions = mAdditionsList[i]
            count = additions.size
            for (j in count - 1 downTo 0) {
                val item = additions[j]
                val view = item.itemView
                view.alpha = 1f
                dispatchAddFinished(item)
                additions.removeAt(j)
                if (additions.isEmpty()) {
                    mAdditionsList.remove(additions)
                }
            }
        }
        listCount = mChangesList.size
        for (i in listCount - 1 downTo 0) {
            val changes: ArrayList<ChangeInfo> = mChangesList[i]
            count = changes.size
            for (j in count - 1 downTo 0) {
                endChangeAnimationIfNecessary(changes[j])
                if (changes.isEmpty()) {
                    mChangesList.remove(changes)
                }
            }
        }

        cancelAll(mRemoveAnimations)
        cancelAll(mMoveAnimations)
        cancelAll(mAddAnimations)
        cancelAll(mChangeAnimations)

        dispatchAnimationsFinished()
    }

    fun cancelAll(viewHolders: List<ViewHolder>) {
        for (i in viewHolders.indices.reversed()) {
            viewHolders[i].itemView.animate().cancel()
        }
    }

    override fun canReuseUpdatedViewHolder(
        viewHolder: ViewHolder,
        payloads: List<Any?>
    ): Boolean {
        return payloads.isNotEmpty() || super.canReuseUpdatedViewHolder(viewHolder, payloads)
    }
}