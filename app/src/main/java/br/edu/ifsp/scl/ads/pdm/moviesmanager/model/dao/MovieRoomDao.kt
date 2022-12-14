package br.edu.ifsp.scl.ads.pdm.moviesmanager.model.dao

import androidx.room.*
import br.edu.ifsp.scl.ads.pdm.moviesmanager.model.entity.Movie

@Dao
interface MovieRoomDao {
    companion object Constant {
        const val MOVIE_DATABASE_FILE = "movies_room"
        const val MOVIE_TABLE = "movie"
        const val ID_COLUMN = "id"
        const val NAME_COLUMN = "name"
        const val GRADE_COLUMN = "grade"
    }

    @Insert
    fun createMovie(movie: Movie)

    @Query("SELECT * FROM $MOVIE_TABLE WHERE $ID_COLUMN = :id")
    fun retrieveMovie(id: Int): Movie?

    @Query("SELECT * FROM $MOVIE_TABLE ORDER BY $NAME_COLUMN")
    fun retrieveMovies(): MutableList<Movie>

    @Query("SELECT * FROM $MOVIE_TABLE ORDER BY LOWER($NAME_COLUMN)")
    fun retrieveMoviesOrderName(): MutableList<Movie>

    @Query("SELECT * FROM $MOVIE_TABLE ORDER BY $GRADE_COLUMN DESC")
    fun retrieveMoviesOrderGrade(): MutableList<Movie>

    @Update
    fun updateMovie(movie: Movie): Int

    @Delete
    fun deleteMovie(movie: Movie): Int

}