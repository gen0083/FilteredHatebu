package jp.gcreate.product.filteredhatebu.ui.feeddetail

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.TouchDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.databinding.ViewCommentStatusBinding
import jp.gcreate.product.filteredhatebu.model.HatebuComments

class CommentStatusView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    
    private lateinit var binding: ViewCommentStatusBinding
    
    init {
        if (isInEditMode) {
            LayoutInflater.from(context).inflate(R.layout.view_comment_status, this, true)
        } else {
            binding = ViewCommentStatusBinding.inflate(LayoutInflater.from(context), this, true)
            doOnPreDraw {
                val rect = Rect()
                binding.container.getDrawingRect(rect)
                binding.container.touchDelegate = TouchDelegate(rect, binding.icon)
            }
        }
    }
    
    fun setCommentStatus(status: HatebuComments) {
        when (status) {
            is HatebuComments.Loading  -> showLoading()
            is HatebuComments.Error    -> showError()
            is HatebuComments.Disallow -> showDisallow()
            is HatebuComments.Empty    -> showCommentCount(0)
            is HatebuComments.Comments -> showCommentCount(status.comments.size)
        }
    }
    
    override fun setOnClickListener(l: OnClickListener?) {
        binding.icon.setOnClickListener(l)
    }
    
    private fun showLoading() {
        canBeClickable(false)
        binding.progressBar.isVisible = true
        binding.commentGroup.isInvisible = true
    }
    
    private fun showError() {
        canBeClickable(true)
        binding.statusMessage.setText(R.string.fetch_comment_error)
        binding.progressBar.isGone = true
        binding.commentGroup.isVisible = true
    }
    
    private fun showDisallow() {
        canBeClickable(false)
        binding.statusMessage.setText(R.string.disallow_comments)
        binding.progressBar.isGone = true
        binding.commentGroup.isVisible = true
    }
    
    private fun showCommentCount(count: Int) {
        canBeClickable(count != 0)
        binding.statusMessage.text = count.toString()
        binding.progressBar.isGone = true
        binding.commentGroup.isVisible = true
    }
    
    private fun canBeClickable(isClickable: Boolean) {
        binding.container.isClickable = isClickable
        binding.icon.isClickable = isClickable
    }
}