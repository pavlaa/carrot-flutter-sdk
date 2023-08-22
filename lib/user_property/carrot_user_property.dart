import 'package:carrotquest_sdk/user_property/user_property.dart';

class CarrotUserProperty extends UserProperty {
  final CarrotProperty property;
  CarrotUserProperty({required this.property, required super.value})
      : super(
          name: "\$${property.name}",
        );

  CarrotUserProperty copyWith({required String newValue}) =>
      CarrotUserProperty(property: property, value: newValue);
}

enum CarrotProperty {
  name,
  phone,
  email,
}
