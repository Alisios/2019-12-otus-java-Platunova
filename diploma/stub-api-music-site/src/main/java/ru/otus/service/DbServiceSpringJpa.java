package ru.otus.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.backend.model.ConcertRestModel;
import ru.otus.repository.DaoConcertRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class DbServiceSpringJpa implements DbService {

    private final DaoConcertRepository daoConcertRepository;

    @Override
    public Optional<ConcertRestModel> getConcertById(long id) {
        return daoConcertRepository.findById(id);
    }

    @Override
    public long saveConcert(ConcertRestModel concert) {
        if (concert == null)
            throw new IllegalArgumentException("Object for saving must not be null!");
        return daoConcertRepository.save(concert).getId();
    }

    @Override
    public List<ConcertRestModel> getAllConcerts() {
        return daoConcertRepository.findAll();
    }

    @Override
    public void deleteConcertByArtist(String artist) {
        daoConcertRepository.deleteByArtist(artist);
    }

    @Override
    public void deleteConcertById(long id) {
        daoConcertRepository.deleteById(id);
    }

    @Override
    public List<ConcertRestModel> findTickets(String artist, String date, String place) {
        return daoConcertRepository.findTickets(artist, date, place);
    }


    @Override
    public List<ConcertRestModel> getConcertByArtist(String artist) {
        return daoConcertRepository.findByArtistContainingIgnoreCase(artist);
    }

    @Override
    public List<ConcertRestModel> saveAll(List<ConcertRestModel> concerts){
        return (List<ConcertRestModel>) daoConcertRepository.saveAll(concerts);
    }

    @Override
    public void deleteAllConcerts() {
        daoConcertRepository.deleteAll();
    }
}
