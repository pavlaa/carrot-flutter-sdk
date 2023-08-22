import 'package:carrotquest_sdk/user_property/user_property.dart';

class EcommerceUserProperty extends UserProperty {
  final EcommerceProperty property;
  EcommerceUserProperty({required this.property, required super.value})
      : super(
          name: "\$${property.name}",
        );

  EcommerceUserProperty copyWith({required String newValue}) =>
      EcommerceUserProperty(property: property, value: newValue);
}

enum EcommerceProperty {
  cart_amount,
  viewed_products,
  cart_items,
  last_order_status,
  last_payment,
  revenue,
  profit,
  group,
  discount,
  orders_count,
  ordered_items,
  ordered_categories,
  viewed_categories,
}
