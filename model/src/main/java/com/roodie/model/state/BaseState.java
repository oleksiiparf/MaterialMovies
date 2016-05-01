package com.roodie.model.state;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.roodie.model.network.NetworkError;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Roodie on 24.06.2015.
 */
public interface BaseState {

    void registerForEvents(Object receiver);

    void unregisterForEvents(Object receiver);

    class BaseEvent {
        public final int callingId;

        public BaseEvent(int callingId) {
            this.callingId = callingId;
        }
    }

    class BaseArgumentEvent<T> extends BaseEvent {
        public final T item;

        public BaseArgumentEvent(int callingId, T item) {
            super(callingId);
            this.item = Preconditions.checkNotNull(item, "item cannot be null");
        }
    }

    class DoubleArgumentEvent<M, V> extends BaseArgumentEvent<M> {
        public final V secondaryItem;

        public DoubleArgumentEvent(int callingId, M item, V secondary) {
            super(callingId, item);
            this.secondaryItem = secondary;
        }
    }

    abstract class PaginatedResult<T> implements Serializable {
        public List<T> items;
        public int page;
        public int totalPages;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            PaginatedResult that = (PaginatedResult) o;
            return Objects.equal(items, that.items);
        }

        @Override
        public int hashCode() {
            return items != null ? items.hashCode() : 0;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (T item: items) {
               sb.append(item.toString());
            }
            sb.append("Page: ").append(page).append(" total pages: ").append(totalPages);
            return sb.toString();
        }

    }

    class ShowLoadingProgressEvent {
        public final int callingId;
        public final boolean show;
        public final boolean secondary;

        public ShowLoadingProgressEvent(int callingId, boolean show) {
            this(callingId, show, false);
        }

        public ShowLoadingProgressEvent(int callingId, boolean show, boolean secondary) {
            this.callingId = callingId;
            this.show = show;
            this.secondary = secondary;
        }
    }

    class ShowRelatedLoadingProgressEvent extends ShowLoadingProgressEvent {
        public ShowRelatedLoadingProgressEvent(int callingId, boolean show) {
            super(callingId, show);
        }
    }

    class ShowCreditLoadingProgressEvent extends ShowLoadingProgressEvent {
        public ShowCreditLoadingProgressEvent(int callingId, boolean show) {
            super(callingId, show);
        }
    }

    class ShowVideosLoadingProgressEvent extends ShowLoadingProgressEvent {
        public ShowVideosLoadingProgressEvent(int callingId, boolean show) {
            super(callingId, show);
        }
    }

    class ShowTvSeasonLoadingProgressEvent extends ShowLoadingProgressEvent {
        public ShowTvSeasonLoadingProgressEvent(int callingId, boolean show) {
            super(callingId, show);
        }
    }

    class OnErrorEvent {
        public final int callingId;
        public final NetworkError error;

        public OnErrorEvent(int callingId, NetworkError error) {
            this.callingId = callingId;
            this.error = error;
        }
    }

}
