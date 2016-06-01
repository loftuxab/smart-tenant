package se.loftux.modules.smarttenant.overrides.exceptions;

/**
 *  Checked exception thrown when multiple users with the same email address
 *  are found in the repository database and/or Lucene/Solr index
 * 
 * @author tomrun (Tommy Runesson <tommy.runesson@cad-q.se>) 
 */
public class MultipleUsersWithSameEmailException extends Exception
{
    private static final long serialVersionUID = 8135606292110337529L;

    public MultipleUsersWithSameEmailException()
    {
        super();
    }

    public MultipleUsersWithSameEmailException(String message)
    {
        super(message);
    }

    public MultipleUsersWithSameEmailException(String message, Exception ex)
    {
        super(message, ex);
    }
}
