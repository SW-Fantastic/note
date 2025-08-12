package org.swdc.note.core.entities;


import jakarta.persistence.*;
import java.net.URI;
import java.net.URL;
import java.util.regex.Pattern;

@Entity
public class CollectionFocus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String host;

    @Column(columnDefinition = "text")
    private String  urlMatch;

    @Column(columnDefinition = "text")
    private String selector;

    public Long getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrlMatch() {
        return urlMatch;
    }

    public void setUrlMatch(String urlMatch) {
        this.urlMatch = urlMatch;
    }

    public boolean isMatched(String matchTarget) {
        try {

            URL url = new URI(getUrlMatch()).toURL();
            String urlEx = url.toExternalForm();
            String proto = url.getProtocol();
            String location = urlEx.replace(proto + "://","");
            String prefix = proto + "://";
            String[] parts = location.split("/");

            StringBuilder stringBuilder = new StringBuilder();
            for (int idx = 0; idx < parts.length; idx ++) {
                if (!stringBuilder.isEmpty()) {
                    stringBuilder.append("/");
                }
                if (parts[idx].equals("*")) {
                    stringBuilder.append("[\\S]+");
                } else {
                    stringBuilder.append(parts[idx]);
                }
            }

            stringBuilder.insert(0,prefix);

            Pattern pattern = Pattern.compile(stringBuilder.toString());
            return pattern.matcher(matchTarget).find();
        } catch (Exception e) {
            return false;
        }
    }

}
