package com.siva.insuris.model;

import java.util.ArrayList;

public class MoviesDetailsModel {
    private String tagline;
    private int runtime;
    private String status;
    private String release_date;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getRelease_date() {
        return release_date;
    }
    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getTagline() {
        return tagline;
    }
    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public int getRuntime() {
        return runtime;
    }
    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }


    public ArrayList<Genres> genres;
    public ArrayList<Production> production_companies;

    public class Genres {
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        private String name;
    }

    public class Production {
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        private String name;
    }
}
