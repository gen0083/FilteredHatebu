package jp.gcreate.product.filteredhatebu.ui.editfilter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.databinding.ItemFilterBinding;
import jp.gcreate.product.filteredhatebu.model.UriFilter;
import jp.gcreate.product.filteredhatebu.ui.common.DataBindingViewHolder;

/**
 * Copyright 2016 G-CREATE
 */

public class FilterAdapter extends RecyclerView.Adapter<DataBindingViewHolder<ItemFilterBinding>> {
    private Context         context;
    private RecyclerView    recyclerView;
    private ItemTouchHelper touchHelper;
    private FilterEditPresenter presenter;

    public FilterAdapter(Context context, FilterEditPresenter presenter) {
        this.context = context;
        touchHelper = new SwipeDismissTouchHelper();
        this.presenter = presenter;
    }

    @Override
    public DataBindingViewHolder<ItemFilterBinding> onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_filter, parent, false);
        return new DataBindingViewHolder<>(v);
    }

    @Override
    public void onBindViewHolder(DataBindingViewHolder<ItemFilterBinding> holder, int position) {
        ItemFilterBinding binding   = holder.getBinding();
        UriFilter         filter    = presenter.getItem(position);
        boolean           isDeleted = presenter.isDeleted(position);
        binding.setItem(filter);
        binding.setIsDeleted(isDeleted);
        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.undoDelete();
            }
        });
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        touchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        touchHelper.attachToRecyclerView(null);
        this.recyclerView = null;
    }

    private class SwipeDismissTouchHelper extends ItemTouchHelper {
        private SwipeDismissTouchHelper() {
            this(new ItemTouchHelper.SimpleCallback(0, LEFT | RIGHT) {
                private Drawable deleteIcon;
                private Drawable background;
                private int padding;
                private Paint paint;
                private int iconWidth;
                private int iconHeight;
                private int color;
                private boolean isInitialized = false;

                private void init() {
                    deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
                    paint = new Paint();
                    color = ContextCompat.getColor(context, R.color.colorAccent);
                    background = new ColorDrawable(color);
                    padding = (int) (context.getResources().getDisplayMetrics().density * 16);
                    iconWidth = deleteIcon.getIntrinsicWidth();
                    iconHeight = deleteIcon.getIntrinsicHeight();
                    isInitialized = true;
                }

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                      RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getAdapterPosition();
                    presenter.delete(position);
                }

                @Override
                public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                        RecyclerView.ViewHolder viewHolder,
                                        float dX, float dY, int actionState,
                                        boolean isCurrentlyActive) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState,
                                      isCurrentlyActive);
                    // draw background of itemView to notify that this action means delete item to user
                    if (dX == 0) return;
                    if (!isInitialized) {
                        init();
                    }
                    int left  = dX > 0 ? 0        : (int) dX;
                    int right = dX > 0 ? (int) dX : viewHolder.itemView.getRight();
                    int top = viewHolder.itemView.getTop();
                    int bottom = viewHolder.itemView.getBottom();
                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                    int margin = (bottom - top - iconHeight) / 2;
                    int dTop = top + margin;
                    int dBottom = bottom - margin;
                    int dLeft = dX > 0 ? padding : right - padding - iconWidth;
                    int dRight = dX > 0 ? padding + iconWidth : right - padding;

                    deleteIcon.setBounds(dLeft, dTop, dRight, dBottom);
                    deleteIcon.draw(c);
                }
            });
        }

        private SwipeDismissTouchHelper(Callback callback) {
            super(callback);
        }
    }
}
