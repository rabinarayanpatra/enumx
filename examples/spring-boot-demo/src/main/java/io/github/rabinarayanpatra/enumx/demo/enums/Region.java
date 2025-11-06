package io.github.rabinarayanpatra.enumx.demo.enums;

import io.github.rabinarayanpatra.enumx.annotations.EnumApi;
import io.github.rabinarayanpatra.enumx.annotations.Expose;
import io.github.rabinarayanpatra.enumx.annotations.Filterable;

@EnumApi(path = "regions", keyField = "code", includeAllFields = false)
public enum Region {
    UNITED_STATES("US", "United States", Continent.NORTH_AMERICA),
    CANADA("CA", "Canada", Continent.NORTH_AMERICA),
    INDIA("IN", "India", Continent.ASIA),
    UNITED_KINGDOM("UK", "United Kingdom", Continent.EUROPE);

    @Expose
    private final String code;

    @Expose
    private final String name;

    @Filterable
    private final Continent continent;

    Region(String code, String name, Continent continent) {
        this.code = code;
        this.name = name;
        this.continent = continent;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Continent getContinent() {
        return continent;
    }

    public enum Continent {
        AFRICA,
        ANTARCTICA,
        ASIA,
        EUROPE,
        NORTH_AMERICA,
        OCEANIA,
        SOUTH_AMERICA
    }
}
