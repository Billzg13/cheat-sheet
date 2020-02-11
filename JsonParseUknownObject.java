 @PostMapping("/manufacturersPending")
    public ResponseEntity<ObjectNode> createPendingManufacturer(@RequestBody ObjectNode body, Authentication authentication) {
        var userId = (String) authentication.getPrincipal();
        var owners = new BasicDBList();
        owners.add(new ObjectId(userId));
        /*
        JsonNode addressJson = body.get("address");
        Iterator<String> fieldNamesAddress = addressJson.fieldNames();
        Document address = new Document();

        JsonNode contactJson = body.get("contact");
        Iterator<String> fieldNamesContact = contactJson.fieldNames();
        Document contact = new Document();

        while (fieldNamesAddress.hasNext()) {
            String fieldName = fieldNamesAddress.next();
            String field = addressJson.get(fieldName).asText();
            address.append(fieldName, field);
        }
        while (fieldNamesContact.hasNext()) {
            String fieldName = fieldNamesContact.next();
            String field = contactJson.get(fieldName).asText();
            contact.append(fieldName, field);
        } */
        var manufacturerPending = new Document()
                .append("name", body.get("name").asText())
                .append("vat", body.get("vat").asText())
                .append("address", new Document("streetFull", body.get("address").get("streetFull").asText())
                        .append("city", body.get("address").get("city").asText())
                        .append("country", body.get("address").get("country").asText())
                )
                .append("contact", new Document("firstName", body.get("contact").get("firstname").asText())
                        .append("lastName", body.get("contact").get("lastname").asText())
                        .append("phone", body.get("contact").get("phone").asText())
                        .append("email", body.get("contact").get("email").asText())
                )
                .append("owner", owners);
        S.mongo_manufacturersPending().insertOne(manufacturerPending);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/manufacturers")
    public ResponseEntity<ObjectNode> createNewManufacturer(@RequestBody ObjectNode body, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        BasicDBList owners = new BasicDBList();
        owners.add(new ObjectId(userId));

        JsonNode addressJson = body.get("address");
        Iterator<String> fieldNamesAddress = addressJson.fieldNames();
        Document address = new Document();

        JsonNode contactJson = body.get("contact");
        Iterator<String> fieldNamesContact = contactJson.fieldNames();
        Document contact = new Document();

        while (fieldNamesAddress.hasNext()) {
            String fieldName = fieldNamesAddress.next();
            String field = addressJson.get(fieldName).asText();
            address.append(fieldName, field);
        }
        while (fieldNamesContact.hasNext()) {
            String fieldName = fieldNamesContact.next();
            String field = contactJson.get(fieldName).asText();
            contact.append(fieldName, field);
        }

        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.returnDocument(ReturnDocument.BEFORE);
        options.upsert(true);
        options.projection(Projections.include("value"));

        Bson query = Filters.eq("_id", "manufacturers_unr");
        Bson update = Updates.inc("value", 1);
        Document sequence = S.mongo_sequences().findOneAndUpdate(query, update, options);

        if (sequence == null)
            throw new NullPointerException("sequence is null");

        Document manufacturer = new Document()
                .append("unr", sequence.getLong("value"))
                .append("name", body.get("name").asText())
                .append("vat", body.get("vat").asText())
                .append("address", address)
                .append("contact", contact)
                .append("owners", owners);

        S.mongo_manufacturers().insertOne(manufacturer);
        manufacturer.remove("unr");
        S.mongo_manufacturersPending().deleteMany(Filters.and(Filters.eq("name", manufacturer.get("name")), Filters.eq("vat", manufacturer.get("vat"))));
        return ResponseEntity.ok().build();
    }
