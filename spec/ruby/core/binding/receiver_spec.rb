require File.expand_path('../../../spec_helper', __FILE__)
require File.expand_path('../fixtures/classes', __FILE__)

ruby_version_is "2.2" do
  describe "Binding#receiver" do
    it "returns the object to which binding is bound" do
      obj = BindingSpecs::Demo.new(1)
      obj.get_binding.receiver.should == obj

      binding.receiver.should == self
    end
  end
end
