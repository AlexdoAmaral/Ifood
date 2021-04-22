package com.br.ifoodnovo.helpers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {

    // Criando as minhas referencias
    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth refernciaAutenticacao;
    private static StorageReference referenciaStorage;

    // Retornando a instância do Firebase
    public static  DatabaseReference getFirebase(){
        if(referenciaFirebase == null){
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaFirebase;
    }

    // Retornando a instância de FirebaseAuth
    public static FirebaseAuth getReferenciaAutenticacao() {
        if (refernciaAutenticacao == null){
            refernciaAutenticacao = FirebaseAuth.getInstance();
        }
        return refernciaAutenticacao;
    }

    // Retornando a Instância do FirebaseStorage
    public static StorageReference getReferenciaStorage(){
        if (referenciaStorage == null){
            referenciaStorage = FirebaseStorage.getInstance().getReference();
        }
        return referenciaStorage;
    }

}