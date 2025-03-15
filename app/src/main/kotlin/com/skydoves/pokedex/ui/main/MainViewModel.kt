package com.skydoves.pokedex.ui.main

import androidx.annotation.MainThread
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import com.skydoves.bindables.BindingViewModel
import com.skydoves.bindables.asBindingProperty
import com.skydoves.bindables.bindingProperty
import com.skydoves.pokedex.core.model.Pokemon
import com.skydoves.pokedex.core.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val mainRepository: MainRepository,
) : BindingViewModel() {

  @get:Bindable
  var isLoading: Boolean by bindingProperty(false)
    private set

  @get:Bindable
  var toastMessage: String? by bindingProperty(null)
    private set

  private val pokemonFetchingIndex: MutableStateFlow<Int> = MutableStateFlow(0)
  private val searchQuery: MutableStateFlow<String> = MutableStateFlow("")

  // Search-related properties
  @get:Bindable
  var searchText: String by bindingProperty("")
    private set  // Use private set instead of custom setter

  private val pokemonListFlow = pokemonFetchingIndex.flatMapLatest { page ->
    if (searchQuery.value.isNotEmpty()) {
      // If we have a search query, use the search repository method
      mainRepository.searchPokemonByName(
        query = searchQuery.value,
        onStart = { isLoading = true },
        onComplete = { isLoading = false },
        onError = { toastMessage = it }
      )
    } else {
      // Otherwise, fetch the regular list
      mainRepository.fetchPokemonList(
        page = page,
        onStart = { isLoading = true },
        onComplete = { isLoading = false },
        onError = { toastMessage = it },
      )
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @get:Bindable
  val pokemonList: List<Pokemon> by pokemonListFlow.asBindingProperty(viewModelScope, emptyList())

  init {
    Timber.d("init MainViewModel")
  }

  @MainThread
  fun fetchNextPokemonList() {
    if (!isLoading) {
      pokemonFetchingIndex.value++
    }
  }

  @MainThread
  fun onSearchQueryChanged(query: String) {
    if (query != searchQuery.value) {
      // Update both properties separately
      searchText = query
      searchQuery.value = query
      // Reset to page 0 when searching
      pokemonFetchingIndex.value = 0
    }
  }

  @MainThread
  fun clearSearch() {
    searchText = ""
    searchQuery.value = ""
    // Reset to page 0 after clearing search
    pokemonFetchingIndex.value = 0
  }

  // Add this method to handle text changes from the UI
  @MainThread
  fun updateSearchText(query: String) {
    searchText = query
    onSearchQueryChanged(query)
  }
}