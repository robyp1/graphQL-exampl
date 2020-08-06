package com.cadit.main.api;

import java.util.List;

public class Human implements FilmCharacter{
    private final String id;
    private final String name;
    private final List<String> friends;
    private final List<Integer> appearsIn;
    final String homePlanet;

    public Human(String id, String name, List<String> friends, List<Integer> appearsIn, String homePlanet) {
        this.id = id;
        this.name = name;
        this.friends = friends;
        this.appearsIn = appearsIn;
        this.homePlanet = homePlanet;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getFriends() {
        return friends;
    }

    @Override
    public List<Integer> getAppearsIn() {
        return appearsIn;
    }

    public String getHomePlanet() {
        return homePlanet;
    }

    @Override
    public String toString() {
        return "Human{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", friends=" + friends +
                ", appearsIn=" + appearsIn +
                ", homePlanet='" + homePlanet + '\'' +
                '}';
    }
}
