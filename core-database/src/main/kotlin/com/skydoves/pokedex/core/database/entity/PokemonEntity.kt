package com.skydoves.pokedex.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skydoves.pokedex.core.model.Pokemon

@Entity
data class PokemonEntity(
  var page: Int = 0,
  @PrimaryKey val name: String,
  val url: String
) {

  fun toPokemon() = Pokemon(
    page = page,
    name = name,
    url = url
  )

  companion object {
    fun fromPokemon(pokemon: Pokemon) = PokemonEntity(
      page = pokemon.page,
      name = pokemon.name,
      url = pokemon.url
    )
  }
}