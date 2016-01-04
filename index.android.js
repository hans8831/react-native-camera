'use strict';

var React = require('react-native');
var { requireNativeComponent, PropTypes, View } = React;

var NativeAndroidCameraView = requireNativeComponent('CameraViewAndroid', AndroidCameraView);

class AndroidCameraView extends React.Component {
  constructor() {
    super();
    this._onChange = this._onChange.bind(this);
    this.onCaptureCompleted = null;
  }

  _onChange(event: Event) {
    if (event.nativeEvent.type == "camera_capture") {
      if (this.onCaptureCompleted) {
        this.onCaptureCompleted(event.nativeEvent.message);
        this.onCaptureCompleted = null;
      }
    } else if (event.nativeEvent.type == "barcode_capture") {
      if (this.props.onBarCodeRead) {
        this.props.onBarCodeRead(event.nativeEvent.message);
      }
    }
  }

  capture(callback) {
    this.onCaptureCompleted = callback;
    this._root.setNativeProps({
      startCapture: "true"
    })
  }

  render() {
    return (
      <NativeAndroidCameraView
          ref={component => this._root = component}
        {...this.props} onChange={this._onChange}
        values={this.props.values} selected={this.props.selected} />
    );
  }
}

AndroidCameraView.propTypes = {
  ...View.propTypes,
  fileName: PropTypes.string,
  startCapture: PropTypes.string,
  onBarCodeRead: PropTypes.func
};

AndroidCameraView.defaultProps = {
  fileName: "test",
  onBarCodeRead: null
}

module.exports = AndroidCameraView;