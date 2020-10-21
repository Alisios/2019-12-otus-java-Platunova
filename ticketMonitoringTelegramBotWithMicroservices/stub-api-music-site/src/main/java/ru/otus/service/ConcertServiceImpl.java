package ru.otus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.backend.model.ConcertRestModel;
import ru.otus.repository.DaoConcertRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ConcertServiceImpl implements ConcertService {

    private final DaoConcertRepository daoConcertRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<ConcertRestModel> getConcertById(long id) {
        try {
            return daoConcertRepository.findById(id);
        } catch (Exception ex) {
            throw new DbException("Error with finding concert by id " + id, ex);
        }
    }

    @Override
    @Transactional
    public long saveConcert(ConcertRestModel concert) {
        try {
            return daoConcertRepository.save(concert).getId();
        } catch (Exception ex) {
            throw new DbException("Error with saving concert " + (concert == null ? "" : concert.getArtist()), ex);
        }
    }

    @Override
    @Transactional
    public List<ConcertRestModel> getAllConcerts() {
        try {
            return daoConcertRepository.findAll();
        } catch (Exception ex) {
            throw new DbException("Error with findingAll concerts ", ex);
        }
    }

    @Override
    @Transactional
    public void deleteConcertByArtist(String artist) {
        try {
            daoConcertRepository.deleteByArtist(artist);
        } catch (Exception ex) {
            throw new DbException("Error with deleting by artist " + artist, ex);
        }
    }

    @Override
    @Transactional
    public void deleteConcertById(long id) {
        try {

            daoConcertRepository.deleteById(id);
        } catch (Exception ex) {
            throw new DbException("Error with deleting concert by id " + id, ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConcertRestModel> findTickets(String artist, String date, String place) {
        try {
            return daoConcertRepository.findTickets(artist, date, place);
        } catch (Exception ex) {
            throw new DbException("Error with finding tickets by artist " + artist + "and place " + place, ex);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<ConcertRestModel> getConcertByArtist(String artist) {
        try {
            return daoConcertRepository.findByArtistContainingIgnoreCase(artist);
        } catch (Exception ex) {
            throw new DbException("Error with finding concert by artist by artist " + artist, ex);
        }
    }

    @Override
    @Transactional
    public List<ConcertRestModel> saveAll(List<ConcertRestModel> concerts) {
        try {
            return (List<ConcertRestModel>) daoConcertRepository.saveAll(concerts);
        } catch (Exception ex) {
            throw new DbException("Error with saving all concerts", ex);
        }
    }

    @Override
    @Transactional
    public void deleteAllConcerts() {
        try {
            daoConcertRepository.deleteAll();
        } catch (Exception ex) {
            throw new DbException("Error with deleting all concerts", ex);
        }
    }
}
