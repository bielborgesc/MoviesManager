package br.edu.ifsp.scl.ads.pdm.moviesmanager.view

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.scl.ads.pdm.moviesmanager.R
import br.edu.ifsp.scl.ads.pdm.moviesmanager.adapter.MovieAdapter
import br.edu.ifsp.scl.ads.pdm.moviesmanager.controller.MovieRoomController
import br.edu.ifsp.scl.ads.pdm.moviesmanager.databinding.ActivityMainBinding
import br.edu.ifsp.scl.ads.pdm.moviesmanager.model.Constant.EXTRA_MOVIE
import br.edu.ifsp.scl.ads.pdm.moviesmanager.model.Constant.VIEW_MOVIE
import br.edu.ifsp.scl.ads.pdm.moviesmanager.model.entity.Movie


class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var movieList: MutableList<Movie> = mutableListOf()

    private lateinit var movieAdapter: MovieAdapter

    private lateinit var carl: ActivityResultLauncher<Intent>

    private val movieController: MovieRoomController by lazy {
        MovieRoomController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        movieController.getMovies()
        movieAdapter = MovieAdapter(this, movieList)
        amb.moviesLv.adapter = movieAdapter

        carl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val movie = result.data?.getParcelableExtra<Movie>(EXTRA_MOVIE)

                movie?.let { _movie->
                    val position = movieList.indexOfFirst { it.id == _movie.id }
                    val checkName = movieList.indexOfFirst { it.name == _movie.name }
                    if (position != -1) {
                        movieController.editMovie(_movie)
                    } else {
                        if(checkName == -1){
                            movieController.insertMovie(_movie)
                        }else{
                            Toast.makeText(this, "Movie exist in your list !!!", Toast.LENGTH_LONG).show()
                        }
                    }
                    movieAdapter.notifyDataSetChanged()
                }
            }
        }

        registerForContextMenu(amb.moviesLv)

        amb.moviesLv.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val movie = movieList[position]
                val movieIntent = Intent(this@MainActivity, MovieActivity::class.java)
                movieIntent.putExtra(EXTRA_MOVIE, movie)
                movieIntent.putExtra(VIEW_MOVIE, true)
                startActivity(movieIntent)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter_main, menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.addMovieMi -> {
                carl.launch(Intent(this, MovieActivity::class.java))
                true
            }
            R.id.orderPerGrade -> {
                movieController.getMoviesOrderGrade()
                true
            }
            R.id.orderPerName -> {
                movieController.getMoviesOrderName()
                true
            }
            else -> { false }
        }
    }


    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menuInflater.inflate(R.menu.context_menu_main, menu)

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = (item.menuInfo as AdapterView.AdapterContextMenuInfo).position
        val movie = movieList[position]
        return when(item.itemId) {
            R.id.removeMovieMi -> {
                movieController.removeMovie(movie)
                true
            }
            R.id.editMovieMi -> {
                val movieIntent = Intent(this, MovieActivity::class.java)
                movieIntent.putExtra(EXTRA_MOVIE, movie)
                movieIntent.putExtra(VIEW_MOVIE, false)
                carl.launch(movieIntent)
                true
            }
            else -> { false }
        }
    }

    fun updateMovieList(_movieList: MutableList<Movie>) {
        movieList.clear()
        movieList.addAll(_movieList)
        movieAdapter.notifyDataSetChanged()
    }

}