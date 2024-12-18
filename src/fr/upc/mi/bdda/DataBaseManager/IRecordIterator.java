package fr.upc.mi.bdda.DataBaseManager;

import fr.upc.mi.bdda.FileAccess.Record;

/**
 * Interface pour itérer des records de maniere spécifique en fonction des opérations.
 * <br/>(Voir TP7-B2 pour comprendre le fonctionnement plus en détail)
 */
public interface IRecordIterator {

    /**
     * Envois le prochain tuple.
     *
     * @return un tuple.
     */
    Record getNextRecord();

    /**
     * Ferme l'itérateur.
     */
    void close();

    /**
     * Remet le cursor de l'itérateur à 0.
     */
    void reset();

}
