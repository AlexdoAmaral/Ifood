package com.br.ifoodclone.helpers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class usuarioFirebase {
    public static String getIdUsuario(){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return autenticacao.getCurrentUser().getUid();
    }
    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return  usuario.getCurrentUser();
    }
    public static boolean atualizarTipousuario(String tipo){
            try{
                FirebaseUser user = getUsuarioAtual();
                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(tipo).build();
                user.updateProfile(profile);
                return true;
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
    }

}
