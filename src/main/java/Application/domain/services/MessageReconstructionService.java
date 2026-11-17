package Application.domain.services;

import Application.domain.model.DeviceInfo;

import java.util.List;

// reconstruye el mensaje original interceptado a partir de los 3 fragmentos
// (cada dispositivo intercepto el mismo mensaje, pero la interferencia de
// la Piedra Filosofal borro distintas palabras en cada copia, dejando "" en
// su lugar). se parte cada fragmento por espacios y, para cada posicion, se
// toma la primera palabra no vacia entre los 3 dispositivos.
public class MessageReconstructionService {

    public String reconstruct(List<DeviceInfo> deviceInfos) {
        if (deviceInfos == null || deviceInfos.isEmpty()) {
            throw new IllegalArgumentException("se necesita al menos un fragmento de mensaje para reconstruir");
        }

        String[][] wordsByDevice = new String[deviceInfos.size()][];
        int wordCount = 0;
        for (int i = 0; i < deviceInfos.size(); i++) {
            String fragment = deviceInfos.get(i).getMessageFragment();
            String[] words = (fragment == null || fragment.isEmpty()) ? new String[0] : fragment.split(" ", -1);
            wordsByDevice[i] = words;
            wordCount = Math.max(wordCount, words.length);
        }

        StringBuilder reconstructed = new StringBuilder();
        for (int index = 0; index < wordCount; index++) {
            String chosen = "";
            for (String[] words : wordsByDevice) {
                if (index < words.length && words[index] != null && !words[index].isEmpty()) {
                    chosen = words[index];
                    break;
                }
            }
            if (index > 0) {
                reconstructed.append(" ");
            }
            reconstructed.append(chosen);
        }

        return reconstructed.toString().trim();
    }
}
