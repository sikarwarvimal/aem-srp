package com.adobe.ugc.srp.entities

import groovy.transform.CompileStatic
import org.apache.sling.api.resource.ValueMap

@CompileStatic
class CustomerEntity {

    private static final String UUID = "uuid"
    private static final String FIRST_NAME = "firstName"
    private static final String LAST_NAME = "lastName"
    private static final String EMAIL = "email"
    private static final String PASSWORD = "password"

    String uuid
    String firstName
    String lastName
    String email
    String password

    public Map<String, Object> create() {
        final Map<String, Object> valueMap = new HashMap<>()
        uuid = java.util.UUID.randomUUID().toString()
        valueMap.put(UUID, uuid)
        update(valueMap)
        return valueMap
    }

    public void update(final Map<String, Object> valueMap) {
        uuid = valueMap.get(UUID)
        if (firstName) valueMap.put(FIRST_NAME, firstName)
        if (lastName) valueMap.put(LAST_NAME, lastName)
        if (email) valueMap.put(EMAIL, email)
        if (password) valueMap.put(PASSWORD, password)
    }

    public void populate(final ValueMap valueMap) {
        uuid = valueMap.get(UUID)
        firstName = valueMap.get(FIRST_NAME)
        lastName = valueMap.get(LAST_NAME)
        email = valueMap.get(EMAIL)
        password = valueMap.get(PASSWORD)
    }
}
