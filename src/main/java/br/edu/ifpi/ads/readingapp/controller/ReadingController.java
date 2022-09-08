package br.edu.ifpi.ads.readingapp.controller;

import br.edu.ifpi.ads.readingapp.dto.ReadingDto;
import br.edu.ifpi.ads.readingapp.dto.ReadingForm;
import br.edu.ifpi.ads.readingapp.dto.ReadingPageForm;
import br.edu.ifpi.ads.readingapp.dto.ReadingStatusForm;
import br.edu.ifpi.ads.readingapp.service.ReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reading")
public class ReadingController {
    private final ReadingService readingService;

    @PostMapping
    public ResponseEntity<ReadingDto> add(@RequestBody ReadingForm readingForm, HttpServletRequest request){
        return new ResponseEntity<>(readingService.add(readingForm, request), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ReadingDto>> listAll(HttpServletRequest request){
        return new ResponseEntity<>(readingService.listAll(request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeById(@PathVariable Long id, HttpServletRequest request){
        readingService.removeById(id, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/page")
    public ResponseEntity<Void> changePage(@RequestBody ReadingPageForm readingPageForm, HttpServletRequest request){
        readingService.changePage(readingPageForm ,request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/status")
    public ResponseEntity<Void> changeStatus(@RequestBody ReadingStatusForm readingStatusForm, HttpServletRequest request){
        readingService.changeStatus(readingStatusForm, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/timeline")
    public ResponseEntity<Page<ReadingDto>> findAll(Pageable pageable){
        return new ResponseEntity<>(readingService.findAll(pageable), HttpStatus.OK);
    }
}
