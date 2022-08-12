package com.alkemy.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alkemy.entity.MovieEntity;
import com.alkemy.interfaces.CharacterService;
import com.alkemy.interfaces.GenreService;
import com.alkemy.interfaces.MovieService;
import com.alkemy.modelDTO.CharacterDTO;
import com.alkemy.modelDTO.GenreDTO;
import com.alkemy.modelDTO.MovieDTO;
import com.alkemy.modelMapper.MovieMapper;
import com.alkemy.repository.MovieRepository;

@Service
public class MovieServiceImpl implements MovieService{

	MovieRepository movieRepository;
	MovieMapper movieMapper;
	
	CharacterService characterService;
	GenreService genreService;
	
	@Autowired
	public MovieServiceImpl(MovieRepository movieRepository, MovieMapper movieMapper, 
							CharacterService characterService, GenreService genreService) {
		
	this.movieRepository = movieRepository;
	this.movieMapper = movieMapper;
	this.characterService = characterService;
	this.genreService = genreService;
	}
	
	
	@Override
	public MovieDTO getMovieById(Long id, boolean loadAssociatedCharacters, boolean loadAssociatedGenres) {

		MovieEntity movieEntity  = movieRepository.getReferenceById(id);
		MovieDTO result = movieMapper.movieEntity2DTO(movieEntity, loadAssociatedCharacters, loadAssociatedGenres);
		return result;
	}

	@Override
	public List<MovieDTO> getAllMovie(boolean loadAssociatedCharacters, boolean loadAssociatedGenres) {

		List<MovieEntity> movieEntityList = movieRepository.findAll();
		List<MovieDTO> result = movieMapper.movieEntityList2DTOList(movieEntityList, loadAssociatedCharacters, loadAssociatedGenres);
		return result;
	}

	@Override
	public MovieDTO saveMovie(MovieDTO movieDTO, boolean loadAssociatedCharacters, boolean loadAssociatedGenres) {
		
		MovieEntity movieEntity = movieMapper.movieDTO2Entity(movieDTO, true, true);
		MovieEntity movieEntitySaved = movieRepository.save(movieEntity);
		MovieDTO result = movieMapper.movieEntity2DTO(movieEntitySaved, loadAssociatedCharacters, loadAssociatedGenres);
		return result;
	}

	@Override
	public MovieDTO updateMovie(Long id, MovieDTO movieDTO, boolean loadAssociatedCharacters, boolean loadAssociatedGenres) {
		
		movieDTO.setId(id);
		return saveMovie(movieDTO, loadAssociatedCharacters, loadAssociatedGenres);
	}

	@Override
	public void deleteMovieById(Long id) {
		
		movieRepository.deleteById(id);
	}
	
	@Override
	public MovieDTO addCharacter2Movie(Long idMovie, Long idCharacter) {
		
		MovieDTO movieDTO = getMovieById(idMovie, true, true);
		
		boolean characterExistsInMovie = false;
		for (CharacterDTO character : movieDTO.getAssociatedCharacters()) {
			
			if (character.getId() == idCharacter) {
				characterExistsInMovie = true;
			}
		}
		
		if (!characterExistsInMovie) {
			CharacterDTO characterDTO = characterService.getCharacterById(idCharacter, false);
			movieDTO.getAssociatedCharacters().add(characterDTO);
		}
		
		return updateMovie(movieDTO.getId(),movieDTO, true, false);
	}

	@Override
	public MovieDTO removeCharacter2Movie(Long idMovie, Long idCharacter) {
		
		MovieDTO movieDTO = getMovieById(idMovie, true, true);
		List<CharacterDTO> characterListUpdated = new ArrayList<>();
		
		for (CharacterDTO character : movieDTO.getAssociatedCharacters()) {
			
			if (!(character.getId() == idCharacter)) {
				
				characterListUpdated.add(character);
			}
		}
		
		movieDTO.setAssociatedCharacters(characterListUpdated);
		return updateMovie(idMovie, movieDTO, true, false);
	}

	@Override
	public MovieDTO addGenre2Movie(Long idMovie, Long idGenre) {
		
		MovieDTO movieDTO = getMovieById(idMovie, true, true);
		
		boolean genreExistsInMovie = false;
		for (GenreDTO genre : movieDTO.getAssociatedGenres()) {
			
			if (genre.getId() == idGenre) {
				genreExistsInMovie = true;
			}
		}
		
		if (!genreExistsInMovie) {
			GenreDTO genreDTO = genreService.getGenreById(idGenre, false);
			movieDTO.getAssociatedGenres().add(genreDTO);
		}
		
		return updateMovie(movieDTO.getId(), movieDTO, false, true);
	}

	@Override
	public MovieDTO removeGenre2Movie(Long idMovie, Long idGenre) {
		
		MovieDTO movieDTO = getMovieById(idMovie, true, true);
		List<GenreDTO> genreListUpdated = new ArrayList<>();
		
		for (GenreDTO genre : movieDTO.getAssociatedGenres()) {
			
			if (!(genre.getId() == idGenre)) {
				
				genreListUpdated.add(genre);
			}
		}
		
		movieDTO.setAssociatedGenres(genreListUpdated);
		return updateMovie(movieDTO.getId(), movieDTO, false, true);
	}
	
}