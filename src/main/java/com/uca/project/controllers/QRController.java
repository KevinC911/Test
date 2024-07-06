package com.uca.project.controllers;

import com.uca.project.domain.entities.Date;
import com.uca.project.domain.entities.Invitation;
import com.uca.project.domain.entities.QR;
import com.uca.project.domain.entities.User;
import com.uca.project.services.EntryService;
import com.uca.project.services.InvitationService;
import com.uca.project.services.QRService;
import com.uca.project.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/qr")
public class QRController {

    private final UserService userService;

    private final EntryService entryService;

    private final InvitationService invitationService;

    private final QRService qrService;

    public QRController(InvitationService invitationService, QRService qrService, EntryService entryService, UserService userService) {
        this.invitationService = invitationService;
        this.qrService = qrService;
        this.entryService = entryService;
        this.userService = userService;
    }

    @GetMapping("/generate/{id}")
    public ResponseEntity<?> generateQR(@PathVariable String id) {

        String hash;
        boolean checkValidDate = false;
        LocalDateTime now = LocalDateTime.now();
        User user = null;


        Invitation invi = invitationService.findById(UUID.fromString(id));

        if(invi == null){
            return new ResponseEntity<>("Invitacion no encontrada", HttpStatus.NOT_FOUND);
        }

        for(Date date: invi.getDates()){
            if (now.isAfter(date.getStart_datetime()) && now.isBefore(date.getEnd_datetime())) {
                checkValidDate = true;
                break;
            }
        }

        if(!checkValidDate){
            return new ResponseEntity<>("No esta en una fecha permitida", HttpStatus.FORBIDDEN);
        }


        if(invi.getQr() != null ){
            if(invi.getQr().isActive() && now.isBefore(invi.getQr().getFinal_datetime())){
                return new ResponseEntity<>("Codigo QR ya existe, y es valido", HttpStatus.NOT_MODIFIED);
            }
            hash = qrService.reGenerateQR(invi.getQr());
            return new ResponseEntity<>(hash, HttpStatus.CREATED);
        }


        hash = qrService.generateQR(user, invi);
        return new ResponseEntity<>(hash, HttpStatus.CREATED);
    }

    // Solamente usar para usuarios que NO SEAN visitantes
    @GetMapping("/general/generate")
    public ResponseEntity<?> generateGeneralQR() {
        User user = userService.findUserAuthenticated();
        Invitation invi = null;
        String hash;
        LocalDateTime now = LocalDateTime.now();

        if(user.getQr() != null){
            if(user.getQr().isActive() && now.isBefore(user.getQr().getFinal_datetime())){
                return new ResponseEntity<>("Codigo QR ya existe, y es valido", HttpStatus.OK);
            }
            hash = qrService.reGenerateQR(user.getQr());
            return new ResponseEntity<>(hash, HttpStatus.CREATED);
        }

        hash = qrService.generateQR(user, invi);
        return new ResponseEntity<>(hash, HttpStatus.CREATED);
    }

    @PostMapping("/validate/{hash}")
    public ResponseEntity<?> validateQR(@PathVariable String hash) {
        QR qr = qrService.findByHash(hash);
        LocalDateTime now = LocalDateTime.now();

        if(qr == null){
            return new ResponseEntity<>("QR no encontrado", HttpStatus.NOT_FOUND);
        } else if(!qr.isActive()){
            return new ResponseEntity<>("QR invalido", HttpStatus.FORBIDDEN);
        }

        if(now.isAfter(qr.getFinal_datetime())){
            return new ResponseEntity<>("El tiempo valido ha pasado", HttpStatus.FORBIDDEN);
        }

        if(qr.getInvitation() != null){
            if(!qr.getInvitation().isInvitationState()){
                return new ResponseEntity<>("Invitacion no valida", HttpStatus.FORBIDDEN);
            }

            if(qr.getInvitation().isUnique_invitation()){
                invitationService.deactivateInvitation(qr.getInvitation());
            }

            qrService.deActivateQR(qr);
            entryService.createEntry(qr.getInvitation().getUser(), qr.getInvitation().getHome(), now);
            return new ResponseEntity<>("Entrada validada", HttpStatus.OK);

        }

        qrService.deActivateQR(qr);
        entryService.createEntry(qr.getUser(), qr.getUser().getHomes().get(0), now);
        return new ResponseEntity<>("Entrada validada", HttpStatus.OK);
    }
}
