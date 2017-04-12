	public Map<Boolean, String> remove(Resource resource, Map<String, Resource> resourceMap) {
		Map<Boolean, String> toReturn = new HashMap<Boolean, String>();
		/**
		 * Invalid resource:
		 * 1. Missing URI;
		 * 2. Invalid URI;
		 * 3. Owner = "*";
		 */
		if (!resource.uriValid() || resource.getOwner() == "*") {
			toReturn.put(false, "invalid resource");
		} else {
			if (resourceMap.containsKey(resource.getKey()) && resourceMap.get(resource.getKey()).getOwner() == resource.getOwner()) {
				resourceMap.remove(resource.getKey());//The remove operation.
				toReturn.put(true, "success");
			} else {
				/**Cannot remove resource: the resource did not exist.*/
				toReturn.put(false, "cannot remove resource");
			}
		}
		return toReturn;
	}
