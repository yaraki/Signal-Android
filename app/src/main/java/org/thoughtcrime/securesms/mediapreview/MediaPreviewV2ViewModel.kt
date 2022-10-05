package org.thoughtcrime.securesms.mediapreview

import android.net.Uri
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import org.signal.core.util.logging.Log
import org.thoughtcrime.securesms.database.MediaDatabase
import org.thoughtcrime.securesms.util.rx.RxStore

class MediaPreviewV2ViewModel : ViewModel() {
  private val TAG = Log.tag(MediaPreviewV2ViewModel::class.java)
  private val store = RxStore(MediaPreviewV2State())
  private val disposables = CompositeDisposable()
  private val repository: MediaPreviewRepository = MediaPreviewRepository()

  val state: Flowable<MediaPreviewV2State> = store.stateFlowable.observeOn(AndroidSchedulers.mainThread())

  fun fetchAttachments(startingUri: Uri, threadId: Long, sorting: MediaDatabase.Sorting) {
    disposables += store.update(repository.getAttachments(startingUri, threadId, sorting)) { mediaRecords: List<MediaDatabase.MediaRecord>, oldState: MediaPreviewV2State ->
      oldState.copy(
        mediaRecords = mediaRecords,
        loadState = MediaPreviewV2State.LoadState.READY
      )
    }
  }

  fun setShowThread(value: Boolean) {
    store.update { oldState ->
      oldState.copy(showThread = value)
    }
  }

  fun setCurrentPage(position: Int) {
    store.update { oldState ->
      oldState.copy(position = position)
    }
  }

  override fun onCleared() {
    disposables.dispose()
    store.dispose()
  }
}