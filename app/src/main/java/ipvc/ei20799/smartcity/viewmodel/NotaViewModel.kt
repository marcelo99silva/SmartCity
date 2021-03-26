package ipvc.ei20799.smartcity.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ipvc.ei20799.smartcity.dataclasses.Nota
import ipvc.ei20799.smartcity.db.NotaRepository
import kotlinx.coroutines.launch

class NotaViewModel (private val repository: NotaRepository) : ViewModel() {

    // Using LiveData and caching what allNotas returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allNotas: LiveData<List<Nota>> = repository.allNotas

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(nota: Nota) = viewModelScope.launch {
        repository.insert(nota)
    }

    fun deleteNotaById(notaId: Int) = viewModelScope.launch {
        repository.deleteNotaById(notaId)
    }
}

class NotaViewModelFactory(private val repository: NotaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}