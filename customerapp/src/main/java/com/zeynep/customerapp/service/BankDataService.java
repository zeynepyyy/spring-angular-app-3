package com.zeynep.customerapp.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.zeynep.customerapp.model.Account;
import com.zeynep.customerapp.repository.AccountRepository;

@Service
public class BankDataService {

    private final AccountRepository accountRepository;
    private static final String FILE_NAME = "bank_data.ser";

    public BankDataService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    
    public String backupData() {
        List<Account> accounts = accountRepository.findAll();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(accounts);
            return "Tüm banka verileri " + FILE_NAME + " dosyasına başarıyla yedeklendi!";
        } catch (IOException e) {
            return "Yedekleme hatası: " + e.getMessage();
        }
    }

    
    @SuppressWarnings("unchecked")
    public List<Account> loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Account>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Veri okuma hatası: " + e.getMessage());
        }
    }
}